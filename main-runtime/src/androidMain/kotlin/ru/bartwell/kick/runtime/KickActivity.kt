package ru.bartwell.kick.runtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.core.util.WindowStateManager
import ru.bartwell.kick.runtime.core.component.DefaultRootComponent

public class KickActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val modules = Kick.modules
        val startScreenJson = intent.getStringExtra(EXTRA_START_SCREEN)
        val startScreen = if (startScreenJson != null) {
            val serializersModule = SerializersModule {
                polymorphic(Config::class) {
                    modules.forEach { it.registerSubclasses(this) }
                }
            }
            val json = Json { this.serializersModule = serializersModule }
            json.decodeFromString<StartScreen>(startScreenJson)
        } else {
            null
        }
        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            modules = modules,
            startScreen = startScreen,
        )
        setContent {
            App(rootComponent = rootComponent)
        }
    }

    override fun onResume() {
        super.onResume()
        WindowStateManager.getInstance()?.setWindowOpen()
    }

    override fun onPause() {
        super.onPause()
        WindowStateManager.getInstance()?.setWindowClosed()
    }

    public companion object {
        public const val EXTRA_START_SCREEN: String = "extra_start_screen"
    }
}
