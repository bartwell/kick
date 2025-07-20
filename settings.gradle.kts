enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kick"
// Core
include(":main-runtime-stub")
include(":main-core")
include(":main-runtime")
// Sample
include("android")
project(":android").projectDir = file("sample/android")
include("desktop")
project(":desktop").projectDir = file("sample/desktop")
include("shared")
project(":shared").projectDir = file("sample/shared")
// Modules
// SQLite
include(":sqlite-core")
project(":sqlite-core").projectDir = file("module/sqlite/sqlite-core")
include(":sqlite-runtime")
project(":sqlite-runtime").projectDir = file("module/sqlite/sqlite-runtime")
include(":sqlite-sqldelight-adapter")
project(":sqlite-sqldelight-adapter").projectDir = file("module/sqlite/sqlite-sqldelight-adapter")
include(":sqlite-room-adapter")
project(":sqlite-room-adapter").projectDir = file("module/sqlite/sqlite-room-adapter")
include(":sqlite-runtime-stub")
project(":sqlite-runtime-stub").projectDir = file("module/sqlite/sqlite-runtime-stub")
include(":sqlite-sqldelight-adapter-stub")
project(":sqlite-sqldelight-adapter-stub").projectDir = file("module/sqlite/sqlite-sqldelight-adapter-stub")
include(":sqlite-room-adapter-stub")
project(":sqlite-room-adapter-stub").projectDir = file("module/sqlite/sqlite-room-adapter-stub")
// Logging
include(":logging")
project(":logging").projectDir = file("module/logging/logging")
include(":logging-stub")
project(":logging-stub").projectDir = file("module/logging/logging-stub")
// Ktor3
include(":ktor3")
project(":ktor3").projectDir = file("module/network/ktor3")
include(":ktor3-stub")
project(":ktor3-stub").projectDir = file("module/network/ktor3-stub")
// Multiplatform Settings
include(":multiplatform-settings")
project(":multiplatform-settings").projectDir = file("module/settings/multiplatform-settings")
include(":multiplatform-settings-stub")
project(":multiplatform-settings-stub").projectDir = file("module/settings/multiplatform-settings-stub")
// File Explorer
include(":file-explorer")
project(":file-explorer").projectDir = file("module/files/file-explorer")
include(":file-explorer-stub")
project(":file-explorer-stub").projectDir = file("module/files/file-explorer-stub")
