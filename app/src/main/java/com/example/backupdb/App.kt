package com.example.backupdb

import android.app.Application

class App : Application() {
    val pointDataBase by lazy { MainDataBase.getDataBase(this) }
}