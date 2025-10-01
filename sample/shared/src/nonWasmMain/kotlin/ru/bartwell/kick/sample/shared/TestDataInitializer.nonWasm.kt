package ru.bartwell.kick.sample.shared

import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.layout.LayoutModule
import ru.bartwell.kick.module.sqlite.adapter.room.RoomWrapper
import ru.bartwell.kick.module.sqlite.runtime.SqliteModule
import ru.bartwell.kick.sample.shared.database.room.AppDatabase
import ru.bartwell.kick.sample.shared.database.room.DatabaseBuilder

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual fun createRoomModule(context: PlatformContext): Module? {
    val roomDatabase = AppDatabase.create(DatabaseBuilder().createBuilder(context))
    return SqliteModule(RoomWrapper(roomDatabase))
}

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual fun createLayoutModule(context: PlatformContext): Module? = LayoutModule(context)
