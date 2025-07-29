import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension

configure<MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()

    coordinates("ru.bartwell.kick", project.name, extra["libraryVersionName"] as String)

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release")
        )
    )

    pom {
        name = "Kick"
        description = "Kick: Kotlin Inspection & Control Kit. " +
                "A modular Compose Multiplatform toolkit for unified in-app " +
                "inspection and control of logs, network, databases and more."
        inceptionYear = "2025"
        url = "https://github.com/bartwell/kick"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "BArtWell"
                name = "Artem Bazhanov"
                email = "web@bartwell.ru"
            }
        }
        scm {
            url = "https://github.com/bartwell/kick"
            connection = "scm:git:git://github.com/bartwell/kick.git"
            developerConnection = "scm:git:ssh://git@github.com/bartwell/kick.git"
        }
    }
}

configure<SigningExtension> {
    val signingKey: String? = System.getenv("SIGNING_SECRET_KEY")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    } else {
        logger.warn("SIGNING_SECRET_KEY/SIGNING_PASSWORD is empty")
    }
}
