package ru.bartwell.kick.core.util

@JsFun("() => Date.now()")
private external fun jsNow(): Double

@JsFun("millis => new Date(millis).toISOString()")
private external fun jsToIsoString(millis: Double): String

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object DateUtils {
    public actual fun formatLogTime(millis: Long): String {
        val iso = jsToIsoString(millis.toDouble())
        val withoutZ = if (iso.endsWith("Z")) iso.dropLast(1) else iso
        return withoutZ.replace('T', ' ')
    }
    public actual fun currentTimeMillis(): Long = jsNow().toLong()
}
