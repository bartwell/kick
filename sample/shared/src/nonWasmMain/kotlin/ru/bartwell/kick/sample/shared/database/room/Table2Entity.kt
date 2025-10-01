package ru.bartwell.kick.sample.shared.database.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table2")
data class Table2Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val quantity: Int,
    val price: Double,
    val image: ByteArray?,
)
