plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
}
