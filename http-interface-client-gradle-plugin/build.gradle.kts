import java.util.Base64

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.vanniktech.maven.publish")
    id("signing")
}

group = "art.picsell.gradle"
description = "Gradle plugin that generates IDE-only stubs for Spring HTTP interface clients"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.9.25")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.25")
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.ow2.asm:asm-tree:9.7.1")
}

gradlePlugin {
    plugins {
        create("httpInterfaceClientStubs") {
            id = "art.picsell.http-interface-client-stubs"
            displayName = "HTTP Interface Client IDE stubs"
            description = project.description
            implementationClass = "art.picsell.gradle.httpinterface.HttpInterfaceClientGradlePlugin"
        }
    }
}

signing {
    val signingKeyId = findProperty("signing.keyId") as String?
    val signingPassword = findProperty("signing.password") as String?
    val signingKey = findProperty("signingKey") as String?

    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKeyId, String(Base64.getDecoder().decode(signingKey)), signingPassword)
        sign(publishing.publications)
    }
}

mavenPublishing {
    coordinates(group.toString(), "http-interface-client-gradle-plugin", "0.0.0-SNAPSHOT" as String)

    publishToMavenCentral(true)
    signAllPublications()

    pom {
        name.set("HTTP Interface Client Gradle Plugin")
        description.set(project.description)
        url.set("https://github.com/picsell-art/spring-http-interface-clients-starter")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("vonpartridge")
                name.set("Lev Kurashchenko")
                email.set("l.kurashchenko@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/picsell-art/spring-http-interface-clients-starter.git")
            developerConnection.set("scm:git:ssh://github.com/picsell-art/spring-http-interface-clients-starter.git")
            url.set("https://github.com/picsell-art/spring-http-interface-clients-starter")
        }
    }
}
