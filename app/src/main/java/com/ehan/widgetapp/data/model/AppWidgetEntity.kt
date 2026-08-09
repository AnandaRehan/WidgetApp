package com.ehan.widgetapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_widgets")
data class AppWidgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customName: String,
    val originalName: String,
    val packageName: String,
    val iconType: String = "DEFAULT", // "DEFAULT", "COLOR", "EMOJI"
    val iconColorHex: String = "#6366F1",
    val iconEmoji: String = "📱",
    val iconShape: String = "SQUIRCLE", // "CIRCLE", "SQUIRCLE", "ROUNDED_SQUARE"
    val createdAt: Long = System.currentTimeMillis()
)
