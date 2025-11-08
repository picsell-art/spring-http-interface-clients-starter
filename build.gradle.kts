plugins {
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.springframework.boot") version "3.5.7" apply false
    id("com.vanniktech.maven.publish") version "0.34.0" apply false
}

val projectVersion = "0.0.4"

allprojects {
    version = projectVersion

    repositories {
        mavenCentral()
    }
}
