package com.mf.weatherapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("SearchQueries")
data class SearchQueries( @ColumnInfo(name = "SearchQuery") val searchQuery: String, @PrimaryKey(autoGenerate = true) var id: Int = 0,) {

}