package ru.bartwell.kick.module.logging.feature.table.util

import kotlin.test.Test
import kotlin.test.assertEquals

class BracketLabelExtractorTest {
    private val extractor = BracketLabelExtractor()

    @Test
    fun extracts_latin_alphanumeric_labels() {
        assertEquals(
            setOf("Label", "ThisIsLabel", "Label2"),
            extractor.extract("[Label] data [ThisIsLabel] [Label2]")
        )
    }

    @Test
    fun ignores_brackets_with_non_label_content() {
        assertEquals(
            setOf("Label"),
            extractor.extract("[1, 2, 3] [word1 word2] [1/2] [Label] [Label_2] [RuРу]")
        )
    }

    @Test
    fun ignores_empty_or_spaced_brackets() {
        assertEquals(emptySet(), extractor.extract("[] [ Label ] [ ]"))
    }
}
