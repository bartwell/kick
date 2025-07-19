package ru.bartwell.kick.module.logging.core.persist

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.bartwell.kick.module.logging.core.data.LogLevel

@Entity
public data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Long,
    val level: LogLevel,
    val message: String,
)
