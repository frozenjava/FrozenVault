package net.frozendevelopment.frozenpasswords.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.frozendevelopment.frozenpasswords.data.converters.DateConverter
import net.frozendevelopment.frozenpasswords.data.converters.ListConverter
import net.frozendevelopment.frozenpasswords.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenpasswords.data.daos.UserDao
import net.frozendevelopment.frozenpasswords.data.models.ServicePasswordModel
import net.frozendevelopment.frozenpasswords.data.models.UserModel

@Database(entities = [ServicePasswordModel::class, UserModel::class], version = 1)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun servicePasswordDao() : ServicePasswordDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "frozenpasswords_database"
                )
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
