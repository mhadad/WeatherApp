package com.mf.toyotamfarag.ui.data.database

import android.content.Context
import androidx.room.Room
import com.mf.weatherapp.data.dao.SearchQueriesDAO
import com.mf.weatherapp.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun getDB(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "Weather_CC").build()
    }

    @Provides
    fun getNotesDao(db: AppDatabase): SearchQueriesDAO {
        return db.searchQueriesDao()
    }
}

