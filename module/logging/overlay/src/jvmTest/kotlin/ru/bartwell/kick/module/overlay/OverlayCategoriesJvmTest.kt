package ru.bartwell.kick.module.overlay

import org.junit.Before
import org.junit.Test
import ru.bartwell.kick.module.overlay.core.store.DEFAULT_CATEGORY
import ru.bartwell.kick.module.overlay.core.store.OverlayStore
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class OverlayCategoriesJvmTest {

    @Before
    fun setUp() {
        // Ensure clean state for every test
        OverlayStore.clear()
        OverlayStore.selectCategory(DEFAULT_CATEGORY)
    }

    @Test
    fun default_state_isEmptyAndDefaultCategorySelected() {
        assertEquals(DEFAULT_CATEGORY, OverlayStore.selectedCategory.value)
        assertEquals(listOf(DEFAULT_CATEGORY), OverlayStore.categories.value)
        assertTrue(OverlayStore.items.value.isEmpty())
    }

    @Test
    fun set_withoutCategory_writesToDefault() {
        OverlayStore.set("k1", "v1")
        assertEquals(listOf(DEFAULT_CATEGORY), OverlayStore.categories.value)
        assertEquals(listOf("k1" to "v1"), OverlayStore.items.value)
    }

    @Test
    fun set_withCategory_isIsolated_perCategory_and_switchingUpdatesItems() {
        val category = "Performance"
        OverlayStore.set("k1", "v1") // Default
        OverlayStore.set("k2", "v2", category)

        // Still on Default
        assertEquals(DEFAULT_CATEGORY, OverlayStore.selectedCategory.value)
        assertEquals(listOf("k1" to "v1"), OverlayStore.items.value)
        assertTrue(OverlayStore.categories.value.containsAll(listOf(DEFAULT_CATEGORY, category)))

        // Switch to the declared category
        OverlayStore.selectCategory(category)
        assertEquals(listOf("k2" to "v2"), OverlayStore.items.value)
    }

    @Test
    fun selecting_unknown_category_addsIt_and_itemsAreEmpty() {
        OverlayStore.selectCategory("NewCat")
        assertTrue(OverlayStore.categories.value.contains("NewCat"))
        assertTrue(OverlayStore.items.value.isEmpty())
    }

    @Test
    fun set_withEmbeddedCategoryPrefix_routesToCategory_and_usesNormalizedKey() {
        val category = "Performance"
        val key = "$category::fps"

        OverlayStore.set(key, "60")

        OverlayStore.selectCategory(category)
        assertEquals(listOf("fps" to "60"), OverlayStore.items.value)
    }

    @Test
    fun declareCategories_addsThemToCategoriesList() {
        val categories = listOf("Performance", "Analytics")

        OverlayStore.declareCategories(categories)

        assertTrue(OverlayStore.categories.value.containsAll(categories + DEFAULT_CATEGORY))
    }
}
