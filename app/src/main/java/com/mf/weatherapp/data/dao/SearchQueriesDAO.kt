package com.mf.weatherapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mf.weatherapp.data.models.SearchQueries
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchQueriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(searchQuery: SearchQueries)

    @Query("SELECT * FROM SearchQueries ORDER BY id DESC")
    fun getAllSearchQueries(): Flow<List<SearchQueries>>

    @Query("SELECT COUNT(*) AS doExist FROM SearchQueries WHERE SearchQuery = :searchQuery")
    suspend fun doSearchQueryExist(searchQuery: String) : Int
}