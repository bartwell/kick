package ru.bartwell.kick.core.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object DateUtils {
    private val iosFormatter: NSDateFormatter by lazy {
        NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
            locale = NSLocale.Companion.currentLocale
        }
    }

    public actual fun formatLogTime(millis: Long): String {
        @Suppress("MagicNumber")
        val seconds = millis.toDouble() / 1000.0
        val date = NSDate.Companion.dateWithTimeIntervalSince1970(seconds)
        return iosFormatter.stringFromDate(date)
    }

    public actual fun currentTimeMillis(): Long {
        @Suppress("MagicNumber")
        return (NSDate().timeIntervalSince1970 * 1000.0).toLong()
    }
}
