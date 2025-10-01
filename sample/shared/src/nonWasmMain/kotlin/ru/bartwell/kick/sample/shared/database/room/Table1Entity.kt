package ru.bartwell.kick.sample.shared.database.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table1")
data class Table1Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int?,
    val salary: Double?,
    val data: ByteArray?,
)
