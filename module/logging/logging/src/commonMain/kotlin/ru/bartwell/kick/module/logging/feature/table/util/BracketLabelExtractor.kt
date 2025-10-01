package ru.bartwell.kick.module.logging.feature.table.util

public class BracketLabelExtractor : LabelExtractor {
    private val regex = Regex("\\[(.*?)]")

    override fun extract(message: String?): Set<String> {
        if (message.isNullOrEmpty()) return emptySet()
        return regex.findAll(message)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .toSet()
    }
}
