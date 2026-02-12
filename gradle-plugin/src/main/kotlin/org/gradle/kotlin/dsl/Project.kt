package org.gradle.kotlin.dsl

import org.gradle.api.Project
import ru.bartwell.kick.gradle.KickExtension

/**
 * Overrides Kick enabled mode from the `kick { }` block.
 * - [enabled] = true → use full runtime (equivalent to KickEnabled.Enabled)
 * - [enabled] = false → use stub (equivalent to KickEnabled.Disabled)
 *
 * Callable in build.gradle.kts without explicit import (package org.gradle.kotlin.dsl is imported by default).
 * Does not depend on order: safe to call before or after applying the Kick plugin (e.g. in subprojects {}).
 *
 * Priority: CLI -Pkick.enabled > enableKick() > kick { enabled }
 */
fun Project.enableKick(enabled: Boolean) {
    extensions.extraProperties.set(KickExtension.KICK_OVERRIDE_KEY, enabled)
}
