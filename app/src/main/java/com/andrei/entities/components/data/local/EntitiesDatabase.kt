package com.andrei.entities.components.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.andrei.entities.components.data.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Product::class], version = 1)
abstract class AbstractProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {

        @Volatile
        private var INSTANCE: AbstractProductDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AbstractProductDatabase {

            val inst = INSTANCE
            if (inst != null)
                return inst

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AbstractProductDatabase::class.java,
                "abstract_db"
            )
                .addCallback(WordDatabaseCallback(scope))
                .build()
            INSTANCE = instance
            return instance
        }

        private class WordDatabaseCallback(private val scope: CoroutineScope) :
            RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.productDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(productDao: ProductDao) {

            productDao.deleteAll()
        }
    }

}