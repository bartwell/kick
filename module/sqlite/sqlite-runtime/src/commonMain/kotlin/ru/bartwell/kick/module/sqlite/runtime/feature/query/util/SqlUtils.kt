package ru.bartwell.kick.module.sqlite.runtime.feature.query.util

public object SqlUtils {
    /**
     * Heuristically determines whether this SQL string can produce a result set.
     */
    public fun mayReturnRows(sql: String): Boolean {
        // 1. Remove all block comments (/* ... */)
        val noBlockComments = Regex("/\\*[\\s\\S]*?\\*/").replace(sql, " ")
        // 2. Remove all line comments (-- ...)
        val noComments = Regex("--.*?$", RegexOption.MULTILINE).replace(noBlockComments, " ")
        // 3. Trim leading whitespace
        val trimmed = noComments.trimStart()

        // 4. Define commands that definitely return rows
        val readers = setOf("select", "with", "pragma", "values")
        // 5. Detect EXPLAIN SELECT or EXPLAIN QUERY PLAN SELECT
        val explainSelect = Regex("^explain(?:\\s+query\\s+plan)?\\s+select", RegexOption.IGNORE_CASE)
        // 6. Detect RETURNING clause (SQLite 3.35+)
        val returningPattern = Regex("\\breturning\\b", RegexOption.IGNORE_CASE)
        // 7. Extract the first token (up to whitespace or '(')
        val firstToken = trimmed
            .split(Regex("\\s|\\("), limit = 2)[0]
            .lowercase()

        // 8. Aggregate conditions
        val mayReturn = explainSelect.containsMatchIn(trimmed) ||
            returningPattern.containsMatchIn(trimmed) ||
            firstToken in readers

        return mayReturn
    }
}
