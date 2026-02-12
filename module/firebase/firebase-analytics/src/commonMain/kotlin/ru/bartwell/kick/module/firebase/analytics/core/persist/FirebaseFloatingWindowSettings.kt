package ru.bartwell.kick.module.firebase.analytics.core.persist

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.observable.makeObservable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.bartwell.kick.core.data.PlatformContext

private const val KEY_ENABLED = "firebase_floating_window_enabled"
private const val KEY_POSITION_X = "firebase_floating_window_position_x"
private const val KEY_POSITION_Y = "firebase_floating_window_position_y"
private const val KEY_USER_ID = "firebase_analytics_user_id"

@OptIn(ExperimentalSettingsApi::class)
internal object FirebaseFloatingWindowSettings {
    private lateinit var settings: ObservableSettings
    private val userId = MutableStateFlow<String?>(null)

    operator fun invoke(context: PlatformContext) {
        settings = PlatformSettingsFactory.create(
            context = context,
            name = "kick_firebase_analytics_overlay",
        ).makeObservable()
        userId.value = settings.getStringOrNull(KEY_USER_ID)
    }

    fun isEnabled(): Boolean = settings.getBoolean(KEY_ENABLED, false)

    @OptIn(ExperimentalSettingsApi::class)
    fun observeEnabled(): Flow<Boolean> = settings.getBooleanFlow(KEY_ENABLED, false)

    fun setEnabled(value: Boolean) {
        settings.putBoolean(KEY_ENABLED, value)
    }

    fun setPosition(x: Float, y: Float) {
        settings.putFloat(KEY_POSITION_X, x)
        settings.putFloat(KEY_POSITION_Y, y)
    }

    fun getPositionX(): Float = settings.getFloat(KEY_POSITION_X, Float.NaN)

    fun getPositionY(): Float = settings.getFloat(KEY_POSITION_Y, Float.NaN)

    fun observeUserId(): Flow<String?> = userId.asStateFlow()

    fun setUserId(value: String?) {
        if (value == null) {
            settings.remove(KEY_USER_ID)
        } else {
            settings.putString(KEY_USER_ID, value)
        }
        userId.value = value
    }
}
