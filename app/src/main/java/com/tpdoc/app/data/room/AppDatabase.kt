package com.tpdoc.app.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ChecklistItem::class, Perusahaan::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun checklistDao(): ChecklistDao
    abstract fun perusahaanDao(): PerusahaanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS perusahaan (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nama TEXT NOT NULL,
                        npwp TEXT NOT NULL,
                        alamat TEXT NOT NULL DEFAULT '',
                        negara TEXT NOT NULL DEFAULT 'Indonesia',
                        status TEXT NOT NULL DEFAULT 'induk',
                        parentId INTEGER DEFAULT NULL,
                        tahunPajak INTEGER NOT NULL DEFAULT 2024,
                        logoPath TEXT DEFAULT NULL,
                        FOREIGN KEY (parentId) REFERENCES perusahaan(id) ON DELETE SET NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_perusahaan_parentId ON perusahaan(parentId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_perusahaan_npwp ON perusahaan(npwp)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_perusahaan_nama ON perusahaan(nama)")
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tpdoc.db",
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}