package ru.bartwell.kick.module.overlay.core.persists

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.observable.makeObservable
import kotlinx.coroutines.flow.Flow
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.store.DEFAULT_CATEGORY

internal object OverlaySettings {
    private lateinit var settings: ObservableSettings
    private const val KEY_ENABLED = "enabled"
    private const val KEY_SELECTED_CATEGORY = "selected_category"

    @OptIn(ExperimentalSettingsApi::class)
    operator fun invoke(context: PlatformContext) {
        settings = PlatformSettingsFactory.create(context = context, name = "kick_overlay_prefs")
            .makeObservable()
    }

    fun isEnabled(): Boolean = settings.getBoolean(KEY_ENABLED, false)

    @OptIn(ExperimentalSettingsApi::class)
    fun observeEnabled(): Flow<Boolean> = settings.getBooleanFlow(KEY_ENABLED, false)

    fun setEnabled(value: Boolean) {
        settings.putBoolean(KEY_ENABLED, value)
    }

    fun getSelectedCategory(): String = settings.getString(KEY_SELECTED_CATEGORY, DEFAULT_CATEGORY)

    fun setSelectedCategory(value: String) {
        settings.putString(KEY_SELECTED_CATEGORY, value)
    }
}
