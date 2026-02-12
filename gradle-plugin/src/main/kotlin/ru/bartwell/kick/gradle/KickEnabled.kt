package ru.bartwell.kick.gradle

/**
 * Defines whether Kick runtime or stub is used.
 * - [Auto]: use task names to decide (release/production/prod → stub)
 * - [Enabled]: always use full runtime (debug)
 * - [Disabled]: always use stub (release)
 */
enum class KickEnabled {
    Auto,
    Enabled,
    Disabled
}
