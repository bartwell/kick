/**
 * Sample that uses the Kick Gradle plugin (ru.bartwell.kick).
 * Verifies plugin applies correctly: adds dependencies to commonMain and export to Kotlin/Native frameworks.
 *
 * Before first build from clean repo run:
 *   ./gradlew publishToMavenLocal
 * so that the plugin and Kick artifacts are available from mavenLocal.
 */
plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.1.21"
    id("ru.bartwell.kick") version "1.0.0"
}

kick {
    enabledAuto() // or enabled() / disabled()
    modules {
        fileExplorer()
    }
}

kotlin {
    jvm()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
