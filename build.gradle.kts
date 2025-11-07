import java.util.Base64

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    id("io.spring.dependency-management") version "1.1.7"
    id("org.springframework.boot") version "3.5.7" apply false

    id("com.vanniktech.maven.publish") version "0.34.0"
    id("signing")
}

val starterVersion = "0.0.3"
val artifact = "spring-http-interface-clients-starter"
val starterGroup = "art.picsell.starter"
group = starterGroup
version = starterVersion
description = "Spring Boot starter for automatically registered clients for HTTP interfaces via WebClient"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}


sourceSets {
    val main by getting
    val test by getting
    val applicationTest by creating {
        kotlin.setSrcDirs(listOf("src/applicationTest/kotlin"))
        resources.setSrcDirs(listOf("src/applicationTest/resources"))
        compileClasspath += main.output + configurations["testCompileClasspath"]
        runtimeClasspath += output + main.output + configurations["testRuntimeClasspath"]
    }
}

configurations {
    named("applicationTestImplementation") {
        extendsFrom(configurations["testImplementation"])
    }
    named("applicationTestRuntimeOnly") {
        extendsFrom(configurations["testRuntimeOnly"])
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.springframework.boot:spring-boot-starter-validation")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    add("applicationTestImplementation", "org.wiremock:wiremock-standalone:3.9.1")
    add("applicationTestImplementation", "org.springframework.boot:spring-boot-starter-webflux")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Test>("applicationTest") {
    description = "Runs the application tests that rely on WireMock stubs."
    group = "verification"
    val applicationTestSourceSet = sourceSets["applicationTest"]
    testClassesDirs = applicationTestSourceSet.output.classesDirs
    classpath = applicationTestSourceSet.runtimeClasspath
    shouldRunAfter("test")
    useJUnitPlatform()
}

tasks.named("check") {
    dependsOn("applicationTest")
}

signing {
    useInMemoryPgpKeys(
        findProperty("signing.keyId") as String?,
        String(Base64.getDecoder().decode(findProperty("signingKey") as String)),
        findProperty("signing.password") as String?
    )
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("github") {
            groupId = starterGroup
            artifactId = artifact
            version = starterVersion

            from(components["java"])

            pom {
                name.set("Spring HTTP Interface Clients Starter")
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
                properties.put("java.version", "17")
            }
        }
    }

    repositories {
        // GitHub Packages
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/picsell-art/spring-http-interface-clients-starter")

            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

mavenPublishing {
    coordinates(starterGroup, artifact, starterVersion)

    publishToMavenCentral(true)
    signAllPublications()

    pom {
        name.set("Spring HTTP Interface Clients Starter")
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

setDependants("signMavenPublication", "publishGithubPublicationToMavenCentralRepository")
setDependants("plainJavadocJar", "generateMetadataFileFor")
setDependants("publishMavenPublicationToMavenCentralRepository", "signGithubPublication")


fun setDependants(parent: String, child: String) {
    afterEvaluate {
        val signMavenPublication by tasks.named(parent)
        tasks.matching { it.name.startsWith(child) }.configureEach {
            dependsOn(signMavenPublication)
        }
    }
}