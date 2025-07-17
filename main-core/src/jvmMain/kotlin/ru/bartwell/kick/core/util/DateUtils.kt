package ru.bartwell.kick.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object DateUtils {
    private val LOG_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    public actual fun formatLogTime(millis: Long): String = LOG_DATE_FORMAT.format(Date(millis))

    public actual fun currentTimeMillis(): Long = System.currentTimeMillis()
}
