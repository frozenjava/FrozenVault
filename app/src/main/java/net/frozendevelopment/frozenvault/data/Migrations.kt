package net.frozendevelopment.frozenvault.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


// Create security questions table
val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ServicePasswords ADD COLUMN securityQuestions TEXT NOT NULL DEFAULT \"[]\"")
    }
}
