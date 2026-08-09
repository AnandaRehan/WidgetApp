package com.ehan.widgetapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ehan.widgetapp.data.model.AppWidgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppWidgetDao {
    @Query("SELECT * FROM app_widgets ORDER BY createdAt DESC")
    fun getAllWidgets(): Flow<List<AppWidgetEntity>>

    @Query("SELECT * FROM app_widgets WHERE id = :id LIMIT 1")
    suspend fun getWidgetById(id: Long): AppWidgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: AppWidgetEntity): Long

    @Update
    suspend fun updateWidget(widget: AppWidgetEntity)

    @Delete
    suspend fun deleteWidget(widget: AppWidgetEntity)

    @Query("DELETE FROM app_widgets WHERE id = :id")
    suspend fun deleteWidgetById(id: Long)
}
