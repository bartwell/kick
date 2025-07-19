import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
    id("signing")
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
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "stub"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.mainCore)
        }
    }

    explicitApi()
}

android {
    namespace = "ru.bartwell.kick"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val javadocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from(file("empty-javadoc"))
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)
        pom {
            name.set("Delight SQL Viewer")
            description.set("Delight SQL Viewer is a multiplatform library that integrates database " +
                    "viewing and editing into your application")
            url.set("https://github.com/bartwell/delight-sql-viewer")
            licenses {
                license {
                    name.set("Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/bartwell/delight-sql-viewer.git")
                developerConnection.set("scm:git:ssh://github.com/bartwell/delight-sql-viewer.git")
                url.set("https://github.com/bartwell/delight-sql-viewer")
            }
            developers {
                developer {
                    id.set("BArtWell")
                    name.set("Artem Bazhanov")
                    email.set("web@bartwell.ru")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("ossrhUsername") as? String ?: System.getenv("OSSRH_USERNAME")
                password = findProperty("ossrhPassword") as? String ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        findProperty("signingKeyId") as? String ?: System.getenv("SIGNING_KEY_ID"),
        findProperty("signingSecretKey") as? String ?: System.getenv("SIGNING_SECRET_KEY"),
        findProperty("signingPassword") as? String ?: System.getenv("SIGNING_PASSWORD")
    )
    sign(publishing.publications)
}

tasks.withType<PublishToMavenLocal>().configureEach {
    dependsOn(tasks.withType<Sign>())
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.withType<Sign>())
}
