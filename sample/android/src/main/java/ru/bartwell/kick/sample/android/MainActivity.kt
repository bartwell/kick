package ru.bartwell.kick.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.bartwell.kick.core.data.toPlatformContext
import ru.bartwell.kick.sample.shared.App
import ru.bartwell.kick.sample.shared.TestDataInitializer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TestDataInitializer(context = toPlatformContext())
        setContent {
            App()
        }
    }
}
