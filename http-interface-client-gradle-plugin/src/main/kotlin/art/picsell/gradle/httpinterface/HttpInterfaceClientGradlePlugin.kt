package art.picsell.gradle.httpinterface

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class HttpInterfaceClientGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "httpInterfaceClientStubs",
            HttpInterfaceClientStubsExtension::class.java
        )

        project.pluginManager.withPlugin("java") {
            configureJavaProject(project, extension)
        }
    }

    private fun configureJavaProject(
        project: Project,
        extension: HttpInterfaceClientStubsExtension
    ) {
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        // Регистрация задачи генерации стабов
        val stubTaskProvider = project.tasks.register(
            "generateHttpInterfaceClientStubs",
            GenerateHttpInterfaceClientStubsTask::class.java
        ) { task -> with(task){
            onlyIf { extension.enabled.get() }
            inputClassesDirs.from(mainSourceSet.output.classesDirs)
            outputDirectory.set(project.layout.buildDirectory.dir("generated/httpInterfaceClientStubs"))
            stubClassSuffix.set(extension.stubClassSuffix)
            componentAnnotation.set(extension.componentAnnotation)
            componentBeanNamePrefix.set(extension.componentBeanNamePrefix)
        }
        }

        stubTaskProvider.configure {
            task -> task.mustRunAfter("compileJava", "compileKotlin", "kspKotlin")
        }

        project.tasks.named("build") {
                task -> task.dependsOn(stubTaskProvider)
        }

        val stubSourceDir = stubTaskProvider.flatMap { it.outputDirectory }
        mainSourceSet.java.srcDir(stubSourceDir)
        attachToKotlinSourceSet(project, stubSourceDir)

        project.tasks.withType(Jar::class.java).configureEach { jar ->
            val suffix = extension.stubClassSuffix.get()
            jar.exclude("**/*$suffix.class")
            jar.exclude("**/*$suffix.java")
        }
    }

    private fun attachToKotlinSourceSet(
        project: Project,
        stubSourceDir: org.gradle.api.provider.Provider<org.gradle.api.file.Directory>
    ) {
        val kotlinExtension = project.extensions.findByType(KotlinProjectExtension::class.java) ?: return
        val main = kotlinExtension.sourceSets.findByName("main") ?: return
        main.kotlin.srcDir(stubSourceDir)
    }
}
