import io.gitlab.arturbosch.detekt.Detekt
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.jetbrainsCompose).apply(false)
    alias(libs.plugins.publish.plugin)
    alias(libs.plugins.detekt)
}

val detektFormatting = libs.detekt.formatting.get()
val detektRulesCompose = libs.detekt.rules.compose.get()

allprojects {
    ext {
        fun loadProperties(filePath: String): Properties {
            val file = file("$rootDir/$filePath")
            require(file.canRead()) { "Cannot read file: ${file.absolutePath}" }
            return Properties().apply {
                file.inputStream().use { load(it) }
            }
        }

        val settingsProperties = loadProperties("settings.properties")
        val versionProperties = loadProperties("version.properties")
        val isRelease = settingsProperties["isRelease"]?.toString()?.toBooleanStrictOrNull()
            ?: error("Missing or invalid 'isRelease' in settings.properties")
        val libraryVersionName = versionProperties["libraryVersionName"]?.toString()
            ?: error("Invalid version name in version.properties")

        extra.apply {
            set("isRelease", isRelease)
            set("libraryVersionName", libraryVersionName)
        }
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    val projectSource = file(projectDir)
    val configFile = files("$rootDir/config/detekt/detekt.yml")
    val baselineFile = file("$rootDir/config/detekt/baseline.xml")
    val kotlinFiles = "**/*.kt"
    val ignoredFiles = listOf("**/resources/**", "**/build/**")

    fun configureDetektTask(taskName: String, autoFix: Boolean) {
        tasks.register(taskName, Detekt::class) {
            description = "Detekt analysis for all modules"
            parallel = false
            ignoreFailures = true
            autoCorrect = autoFix
            buildUponDefaultConfig = true
            setSource(projectSource)
            baseline.set(baselineFile)
            config.setFrom(configFile)
            include(kotlinFiles)
            exclude(ignoredFiles)
            reports {
                html.required.set(false)
                xml.required.set(true)
                txt.required.set(false)
            }
        }
    }

    configureDetektTask("detektCheckAll", project.hasProperty("detektAutoFix"))
    configureDetektTask("detektFixAll", true)

    dependencies {
        detektPlugins(detektFormatting)
        detektPlugins(detektRulesCompose)
    }
}
