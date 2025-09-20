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

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "overlay"
            isStatic = true
        }
    }

    jvm()

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
            implementation(libs.settings)
            implementation(libs.settings.noArg)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
//            implementation("androidx.lifecycle:lifecycle-runtime-android:2.9.4")
//            implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.4")
//            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
//            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
//            implementation("androidx.savedstate:savedstate:1.3.3")
        }
        appleMain.dependencies {
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }

    explicitApi()
}

android {
    namespace = "ru.bartwell.kick.module.overlay"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }
}
