package ru.bartwell.kick.sample.shared.database.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers

@Database(entities = [Table1Entity::class, Table2Entity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTable1Dao(): Table1Dao
    abstract fun getTable2Dao(): Table2Dao

    companion object {
        fun create(builder: Builder<AppDatabase>): AppDatabase {
            return builder
                .addCallback(object : Callback() {
                    override fun onCreate(connection: SQLiteConnection) {
                        super.onCreate(connection)
                        connection.execSQL(
                            """
                            INSERT INTO table1 (name, age, salary, data)
                            VALUES
                            ('Alexandria Catherine Montgomery-Bentley-Smythe-Wilkinson-Johnson', 25, 50000.50, CAST('12345678' AS BLOB)),
                            ('Bob Smith', 30, 62000.00, CAST('abcdef12' AS BLOB)),
                            ('Charlie Brown', 28, NULL, NULL),
                            ('David Miller', 40, 75000.25, CAST('789abcde' AS BLOB)),
                            ('Eve Davis', 35, 82000.75, NULL),
                            ('Frank Wilson', 45, 95000.00, CAST('456def78' AS BLOB)),
                            ('Grace Lee', 29, 67000.10, CAST('zxcvbnm' AS BLOB)),
                            ('Hank Anderson', 50, NULL, NULL),
                            ('Ivy Thompson', 22, 45000.90, CAST('654321006543210065432100654321006543210065432100654321006543210065432100' AS BLOB)),
                            ('Jack Peterson', 38, 78000.40, NULL)
                            """.trimIndent()
                        )

                        connection.execSQL(
                            """
                            INSERT INTO table2 (title, quantity, price, image)
                            VALUES
                            ('Product A', 100, 9.99, CAST('AABBCCDD' AS BLOB)),
                            ('Product B', 50, 19.95, CAST('11223344' AS BLOB)),
                            ('Product C', 75, 14.50, CAST('DEADBEEF' AS BLOB)),
                            ('Product D', 200, 4.99, CAST('CAFEBABE' AS BLOB)),
                            ('Product E', 120, 29.95, CAST('FEEDFACE' AS BLOB)),
                            ('Product F', 30, 49.99, CAST('BADDCAFE' AS BLOB)),
                            ('Product G', 90, 5.50, CAST('FACEFEED' AS BLOB)),
                            ('Product H', 60, 12.00, CAST('DEADC0DE' AS BLOB)),
                            ('Product I', 15, 99.95, CAST('C0FFEE00' AS BLOB)),
                            ('Product J', 80, 7.25, CAST('0BADF00D' AS BLOB))
                            """.trimIndent()
                        )
                    }
                })
                .fallbackToDestructiveMigration(dropAllTables = true)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.Default)
                .build()
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
