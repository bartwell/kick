import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.publish.plugin)
    id("publish-convention")
}

group = "ru.bartwell.kick"
version = extra["libraryVersionName"] as String

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    @Suppress("OPT_IN_USAGE")
    wasmJs {
        browser()
    }

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "firebase-cloud-messaging-stub"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.mainCore)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.decompose)
            implementation(libs.decompose.extensions.compose)
            implementation(libs.decompose.essenty.lifecycle.coroutines)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
        iosTest.dependencies {
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlin.test)
        }
    }

    explicitApi()
}

android {
    namespace = "ru.bartwell.kick"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
