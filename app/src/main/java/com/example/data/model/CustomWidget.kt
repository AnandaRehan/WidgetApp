package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "custom_widgets")
@JsonClass(generateAdapter = true)
data class CustomWidget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val displayName: String,
    val packageName: String,
    val customIconType: String = "SYSTEM_APP", // SYSTEM_APP, PRESET_VECTOR, COLORED_BADGE
    val customIconValue: String = "", // Preset icon name, color hex, etc.
    val backgroundColorHex: String = "#1E293B",
    val textColorHex: String = "#FFFFFF",
    val shapeType: String = "ROUNDED", // ROUNDED, CIRCLE, SQUIRCLE, GLASS
    val badgeText: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
