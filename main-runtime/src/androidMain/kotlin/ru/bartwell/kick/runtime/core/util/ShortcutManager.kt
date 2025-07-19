package ru.bartwell.kick.runtime.core.util

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.runtime.KickActivity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object ShortcutManager {
    internal actual fun setup(context: PlatformContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context.get().getSystemService(ShortcutManager::class.java)?.let { shortcutManager ->
                val intent = Intent(context.get(), KickActivity::class.java)
                intent.setAction(Intent.ACTION_VIEW)
                val shortcut = ShortcutInfo.Builder(context.get(), id)
                    .setShortLabel(title)
                    .setLongLabel(subtitle)
                    .setIcon(Icon.createWithResource(context.get(), android.R.drawable.ic_menu_info_details))
                    .setIntent(intent)
                    .build()
                shortcutManager.dynamicShortcuts = listOf(shortcut)
            }
        }
    }
}
