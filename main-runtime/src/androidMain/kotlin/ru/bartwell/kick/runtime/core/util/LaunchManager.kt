package ru.bartwell.kick.runtime.core.util

import android.content.Intent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.runtime.KickActivity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchManager {

    actual fun launch(
        context: PlatformContext,
        modules: List<Module>,
        startScreen: StartScreen?,
    ) {
        val intent = Intent(context.get(), KickActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (startScreen != null) {
            val serializersModule = SerializersModule {
                polymorphic(Config::class) {
                    modules.forEach { it.registerSubclasses(this) }
                }
            }
            val json = Json { this.serializersModule = serializersModule }
            intent.putExtra(KickActivity.EXTRA_START_SCREEN, json.encodeToString(startScreen))
        }
        context.get().startActivity(intent)
    }
}
