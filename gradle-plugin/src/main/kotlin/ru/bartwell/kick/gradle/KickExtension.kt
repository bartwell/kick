package ru.bartwell.kick.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty

/**
 * Extension name: `kick`
 * DSL:
 * kick {
 *     enabledAuto() // or enabled() / disabled()
 *     modules { fileExplorer(); ktor3() }
 * }
 */
abstract class KickExtension(
    private val project: Project
) {
    internal abstract val enabled: Property<KickEnabled>
    abstract val version: Property<String>
    internal abstract val modules: SetProperty<KickModule>

    /**
     * For tests only: if set, used instead of gradleProperty("kick.enabled") so CLI tests can run without a real Gradle property.
     * Production code never sets this.
     */
    var testCliOverrideProvider: Provider<out String?>? = null

    init {
        enabled.convention(KickEnabled.Auto)
        // Default: plugin's Implementation-Version (set from version.properties when building the plugin), or "1.0.0" when plugin not applied (e.g. tests).
        version.convention(project.provider { resolveDefaultVersion() })
        modules.convention(emptySet())
    }

    private fun resolveDefaultVersion(): String {
        return project.plugins.findPlugin("ru.bartwell.kick")?.javaClass?.`package`?.implementationVersion ?: "1.0.0"
    }

    /** Auto: use task names to decide (release/production/prod → stub). */
    fun enabledAuto() = enabled.set(KickEnabled.Auto)

    /** Always use full runtime (debug). */
    fun enabled() = enabled.set(KickEnabled.Enabled)

    /** Always use stub (release). */
    fun disabled() = enabled.set(KickEnabled.Disabled)

    /** Configure modules via type-safe method calls. */
    fun modules(block: KickModulesScope.() -> Unit) = KickModulesScope(modules).block()

    /**
     * Effective enabled: CLI > enableKick() (extraProperties) > kick { enabledAuto() / enabled() / disabled() }
     */
    fun effectiveEnabled(): KickEnabled {
        val cli = parseCliOverride()
        return if (cli != null) {
            cli
        } else {
            val extra = project.extensions.extraProperties.properties[KICK_OVERRIDE_KEY] as? Boolean
            if (extra != null) {
                if (extra) {
                    KickEnabled.Enabled
                } else {
                    KickEnabled.Disabled
                }
            } else {
                enabled.get()
            }
        }
    }

    /**
     * Reads only from Gradle property (-Pkick.enabled or gradle.properties), not from extraProperties.
     * In tests, testCliOverrideProvider can be set to supply a value without a real gradle property.
     */
    private fun parseCliOverride(): KickEnabled? {
        val raw = testCliOverrideProvider?.getOrNull()
            ?: project.providers.gradleProperty("kick.enabled").getOrNull()
            ?: return null
        return when (raw.lowercase()) {
            "true" -> KickEnabled.Enabled
            "false" -> KickEnabled.Disabled
            else -> throw GradleException(
                "Kick: invalid -Pkick.enabled value. Use true or false."
            )
        }
    }

    /**
     * isRelease for dependency/export choice: stub when release, runtime when debug.
     * - Enabled -> false (runtime)
     * - Disabled -> true (stub)
     * - Auto -> from task names (release/production/prod -> true)
     */
    fun isRelease(): Boolean {
        val eff = effectiveEnabled()
        return when (eff) {
            KickEnabled.Enabled -> false
            KickEnabled.Disabled -> true
            KickEnabled.Auto -> {
                val taskNames = project.gradle.startParameter.taskNames
                val releaseSubstrings = listOf("release", "production", "prod")
                taskNames.any { taskName ->
                    releaseSubstrings.any { sub ->
                        sub in taskName.lowercase()
                    }
                }
            }
        }
    }

    fun kickVersion(): String = version.get()

    fun validate() {
        val mods = modules.getOrElse(emptySet())
        if (mods.isEmpty()) {
            throw GradleException(
                "Kick: modules { ... } is required. Example: kick { modules { fileExplorer(); ktor3() } }"
            )
        }
    }

    companion object {
        /** Key used in extraProperties by enableKick(); read here so order of application does not matter. */
        const val KICK_OVERRIDE_KEY = "kick.enabled.override"
    }
}
