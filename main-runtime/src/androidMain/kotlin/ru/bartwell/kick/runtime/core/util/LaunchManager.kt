package ru.bartwell.kick.runtime.core.util

import android.content.Intent
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.runtime.KickActivity
import ru.bartwell.kick.runtime.core.data.LaunchInMemoryStorage
import ru.bartwell.kick.runtime.core.domain.LaunchStorage

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchManager {

    internal val launchStorage: LaunchStorage = LaunchInMemoryStorage()

    actual fun launch(context: PlatformContext, modules: List<Module>) {
        launchStorage.modules = modules
        val intent = Intent(context.get(), KickActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.get().startActivity(intent)
    }
}
