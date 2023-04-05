package com.example.backupdb

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

@Database(entities = [Point::class], version = 1)
abstract class MainDataBase : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null

        fun getDataBase(contex: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    contex.applicationContext,
                    MainDataBase::class.java, "test1.db"
                ).
                    //createFromFile(File("/storage/emulated/0/Documents/test1.db")).
                build()
                instance
            }
        }
    }
}