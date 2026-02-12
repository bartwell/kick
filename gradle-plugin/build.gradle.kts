import java.util.Properties

plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "ru.bartwell.kick"

val versionProperties = Properties().apply {
    file("${rootProject.projectDir}/version.properties").inputStream().use { load(it) }
}
val pluginVersion: String = versionProperties["libraryVersionName"]?.toString() ?: "1.0.0"

version = pluginVersion

gradlePlugin {
    plugins {
        create("kick") {
            id = "ru.bartwell.kick"
            implementationClass = "ru.bartwell.kick.gradle.KickGradlePlugin"
            displayName = "Kick Kotlin Multiplatform"
            description = "Configures Kick runtime/stub dependencies and Kotlin/Native framework exports for KMP"
        }
    }
}

val kotlinVersion = libs.versions.kotlin.get()

dependencies {
    compileOnly(gradleApi())
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

    testImplementation(gradleApi())
    testImplementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    testImplementation(gradleTestKit())
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
}

tasks.jar {
    manifest.attributes["Implementation-Version"] = pluginVersion
}

publishing {
    repositories {
        mavenLocal()
    }
}

tasks.test {
    useJUnitPlatform()
}
