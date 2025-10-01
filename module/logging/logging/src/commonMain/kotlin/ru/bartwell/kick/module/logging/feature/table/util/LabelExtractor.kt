package ru.bartwell.kick.module.logging.feature.table.util

public interface LabelExtractor {
    public fun extract(message: String?): Set<String>
}
