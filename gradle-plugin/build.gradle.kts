plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("maven-publish")
    alias(libs.plugins.publish.plugin)
    id("publish-convention")
}

group = "ru.bartwell.kick"
version = extra["libraryVersionName"] as String

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
    manifest.attributes["Implementation-Version"] = version
}

tasks.test {
    useJUnitPlatform()
}
