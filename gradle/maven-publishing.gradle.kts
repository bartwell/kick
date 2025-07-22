import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

plugins.withId("maven-publish") {
    val javadocJar = tasks.findByName("javadocJar") as? Jar
        ?: tasks.register("javadocJar", Jar::class.java) {
            archiveClassifier.set("javadoc")
            from(file("empty-javadoc"))
        }.get()

    extensions.configure<PublishingExtension>("publishing") {
        publications.withType<MavenPublication>().configureEach {
            artifact(javadocJar)
            pom {
                name.set("Kick")
                description.set(
                    "Kick: Kotlin Inspection & Control Kit. " +
                            "A modular Compose Multiplatform toolkit for unified in-app " +
                            "inspection and control of logs, network, databases and more."
                )
                url.set("https://github.com/bartwell/kick")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/bartwell/kick.git")
                    developerConnection.set("scm:git:ssh://github.com/bartwell/kick.git")
                    url.set("https://github.com/bartwell/kick")
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
    }
}
