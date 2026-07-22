package ru.bartwell.kick.module.logging.feature.table.util

public class BracketLabelExtractor : LabelExtractor {
    private val regex = Regex("\\[([A-Za-z0-9]+)]")

    override fun extract(message: String?): Set<String> {
        if (message.isNullOrEmpty()) return emptySet()
        return regex.findAll(message)
            .map { it.groupValues[1] }
            .toSet()
    }
}
