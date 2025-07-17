package ru.bartwell.kick.feature.query.mapper

import ru.bartwell.kick.core.mapper.CursorWrapper
import ru.bartwell.kick.core.mapper.SqlMapper

/**
 * Hack workaround for dynamic column detection when SqlDelight's SqlCursor lacks columnCount
 * or columnNames on Android/iOS.
 *
 * This is a crude solution to avoid infinite loops due to null returns when accessing out-of-bounds
 * columns. We probe up to 10 consecutive nulls or exceptions, reset the probe when actual data
 * is found, and then trim trailing nulls after the last non-null column.
 */

private const val ATTEMPTS_COUNT = 10

internal class QuerySqlMapper : SqlMapper<List<String?>> {
    override fun map(cursor: CursorWrapper<*>): List<String?> {
        // Result list of column values (may contain nulls)
        val result = mutableListOf<String?>()
        // Current column index to probe
        var index = 0
        // Counts how many consecutive nulls or exceptions have occurred
        var consecutiveNulls = 0
        // Tracks the index of the most recent non-null value
        var lastNonNullIndex = -1

        // Read columns until we encounter 10 consecutive nulls or exceptions
        while (consecutiveNulls < ATTEMPTS_COUNT) {
            val value: String? = try {
                // Attempt to get the string at the current index
                cursor.getString(index)
            } catch (_: Exception) {
                // On exception (out of bounds), treat as null
                null
            }
            result.add(value)

            if (value != null) {
                // Reset counter when a real value is found
                consecutiveNulls = 0
                lastNonNullIndex = index
            } else {
                // Increment counter for nulls/exceptions
                consecutiveNulls++
            }
            index++
        }

        // Trim any trailing null entries after the last real column
        return if (lastNonNullIndex >= 0) {
            result.subList(0, lastNonNullIndex + 1)
        } else {
            // If no real data found, return empty list
            emptyList()
        }
    }
}
