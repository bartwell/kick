plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain.dependencies {
            implementation(projects.shared)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "ru.bartwell.kick.sample.desktop.MainKt"
    }
}
