import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.bundling.Jar
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

plugins.withId("maven-publish") {
    val javadocJar = tasks.findByName("javadocJar") as? Jar
        ?: tasks.create("javadocJar", Jar::class.java) {
            archiveClassifier.set("javadoc")
            from(file("empty-javadoc"))
        }

    extensions.configure<PublishingExtension>("publishing") {
        publications.withType(MavenPublication::class.java).configureEach {
            artifact(javadocJar)
            pom {
                name.set("Delight SQL Viewer")
                description.set(
                    "Delight SQL Viewer is a multiplatform library " +
                            "that integrates database viewing and editing into your application"
                )
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
                    username = findProperty("ossrhUsername") as String?
                        ?: System.getenv("OSSRH_USERNAME")
                    password = findProperty("ossrhPassword") as String?
                        ?: System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }
}

plugins.withId("signing") {
    extensions.configure<SigningExtension>("signing") {
        useInMemoryPgpKeys(
            findProperty("signingKeyId") as String? ?: System.getenv("SIGNING_KEY_ID"),
            findProperty("signingSecretKey") as String? ?: System.getenv("SIGNING_SECRET_KEY"),
            findProperty("signingPassword") as String? ?: System.getenv("SIGNING_PASSWORD")
        )
        val publishingExtension = project.extensions.getByType(PublishingExtension::class.java)
        sign(*publishingExtension.publications.toTypedArray())
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
    dependsOn(tasks.withType<Sign>())
}
tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.withType<Sign>())
}
