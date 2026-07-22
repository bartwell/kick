import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
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
            baseName = "firebase-analytics"
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
        implementation(libs.settings)
        implementation(libs.settings.make.observable)
        implementation(libs.settings.coroutines)
        implementation(libs.settings.noArg)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.sqldelight.coroutines.extensions)
        implementation(libs.sqldelight.async.extensions)
    }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android.driver)
        }
        appleMain.dependencies {
            implementation(libs.sqldelight.native.driver)
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
    namespace = "ru.bartwell.kick.module.firebaseanalytics"
    compileSdk = 37

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

sqldelight {
    databases {
        create("FirebaseAnalyticsDb") {
            packageName.set("ru.bartwell.kick.module.firebase.analytics.db")
            generateAsync.set(true)
        }
    }
}
