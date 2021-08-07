plugins {
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.graalvm.buildtools.native") version "0.9.1"
    id("org.springframework.boot") version "2.5.3"
    id("org.springframework.experimental.aot") version "0.10.2-SNAPSHOT"
    kotlin("jvm") version "1.5.21"
    kotlin("kapt") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
}

group = "com.gorlah.chat"
version = "latest"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    maven { url = uri("https://repo.spring.io/snapshot") }
    mavenCentral()
}

dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    test {
        useJUnitPlatform()
    }

    bootBuildImage {
        builder = "paketobuildpacks/builder:tiny"
        environment = mapOf("BP_NATIVE_IMAGE" to "true")
        imageName = "gorlah/chat-bot:latest"
        isPublish = false
    }

    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = java.sourceCompatibility.toString()
        }
    }
}