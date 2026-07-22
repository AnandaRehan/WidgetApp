package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomWidget
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomWidgetDao {
    @Query("SELECT * FROM custom_widgets ORDER BY createdAt DESC")
    fun getAllWidgets(): Flow<List<CustomWidget>>

    @Query("SELECT * FROM custom_widgets WHERE id = :id")
    suspend fun getWidgetById(id: Long): CustomWidget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: CustomWidget): Long

    @Update
    suspend fun updateWidget(widget: CustomWidget)

    @Query("DELETE FROM custom_widgets WHERE id = :id")
    suspend fun deleteWidget(id: Long)

    @Query("DELETE FROM custom_widgets")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(widgets: List<CustomWidget>)
}
