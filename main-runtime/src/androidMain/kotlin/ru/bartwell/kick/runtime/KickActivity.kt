package ru.bartwell.kick.runtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import ru.bartwell.kick.Kick
import ru.bartwell.kick.runtime.core.component.DefaultRootComponent

public class KickActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val modules = Kick.modules
        val rootComponent = DefaultRootComponent(componentContext = defaultComponentContext(), modules = modules)
        setContent {
            App(rootComponent = rootComponent)
        }
    }
}
