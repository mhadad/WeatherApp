package com.mf.weatherapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mf.weatherapp.data.dao.SearchQueriesDAO
import com.mf.weatherapp.data.models.SearchQueries

@Database(entities = [SearchQueries::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun searchQueriesDao(): SearchQueriesDAO
}