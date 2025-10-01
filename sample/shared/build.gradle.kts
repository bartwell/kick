import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.room)
}

/*
 The isRelease flag is created here to demonstrate one possible way to switch between
 release and debug builds. In release builds, we use the full-featured library, while in
 debug builds, we use a stub library.
*/
val isRelease = extra["isRelease"] as Boolean

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

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            export(projects.mainCore)
            if (isRelease) {
                export(projects.mainRuntimeStub)
            } else {
                export(projects.mainRuntime)
            }
        }
    }

    jvm()

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val jvmMain by getting
        val wasmJsMain by getting
        val nonWasmMain by creating { dependsOn(commonMain) }
        val nativeMain by creating { dependsOn(nonWasmMain) }
        androidMain.dependsOn(nonWasmMain)
        iosX64Main.dependsOn(nonWasmMain)
        iosArm64Main.dependsOn(nonWasmMain)
        iosSimulatorArm64Main.dependsOn(nonWasmMain)
        jvmMain.dependsOn(nonWasmMain)
        val iosMain by creating { dependsOn(nonWasmMain) }
        iosX64Main.dependsOn(nativeMain)
        iosArm64Main.dependsOn(nativeMain)
        iosSimulatorArm64Main.dependsOn(nativeMain)
        iosMain.dependsOn(nativeMain)
        iosX64Main.dependsOn(iosMain)
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)

        commonMain.dependencies {
            api(projects.mainCore)
            api(projects.sqliteCore)
            if (isRelease) {
                api(projects.mainRuntimeStub)
                api(projects.loggingStub)
                api(projects.multiplatformSettingsStub)
                api(projects.configurationStub)
                api(projects.overlayStub)
                api(projects.ktor3Stub)
                api(projects.sqliteRuntimeStub)
                api(projects.sqliteSqldelightAdapterStub)
                api(projects.fileExplorerStub)
            } else {
                api(projects.mainRuntime)
                api(projects.sqliteRuntime)
                api(projects.sqliteSqldelightAdapter)
                api(projects.logging)
                api(projects.ktor3)
                api(projects.multiplatformSettings)
                api(projects.configuration)
                api(projects.overlay)
                api(projects.fileExplorer)
            }
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.napier)
            implementation(libs.sqldelight.coroutines.extensions)
            api(libs.settings)
            implementation(libs.settings.noArg)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
            implementation(libs.ktor.client.darwin)
        }
        nonWasmMain.dependencies {
            if (isRelease) {
                api(projects.sqliteRoomAdapterStub)
                api(projects.layoutStub)
            } else {
                api(projects.sqliteRoomAdapter)
                api(projects.layout)
            }
            api(libs.room.runtime)
            implementation(libs.room.driver)
        }
        androidMain.dependencies {}
        jvmMain.dependencies {}
        iosMain.dependencies {}
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.driver.sqlite)
            implementation(libs.ktor.client.cio)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js.wasm)
            implementation(libs.sqldelight.web.worker.driver.wasm)
            implementation(libs.sqldelight.async.extensions)
        }
    }
}

android {
    namespace = "ru.bartwell.kick.sample.shared"
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

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

sqldelight {
    databases {
        create("SampleDatabase") {
            packageName.set("ru.bartwell.kick.sample.shared")
            generateAsync.set(true)
        }
    }
}
