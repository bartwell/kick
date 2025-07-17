package ru.bartwell.kick.core.util

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public expect object DateUtils {
    public fun formatLogTime(millis: Long): String
    public fun currentTimeMillis(): Long
}
