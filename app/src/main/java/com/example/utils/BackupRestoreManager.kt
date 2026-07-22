package com.example.utils

import com.example.data.model.CustomWidget
import org.json.JSONArray
import org.json.JSONObject

object BackupRestoreManager {

    fun exportToJson(widgets: List<CustomWidget>): String {
        val jsonArray = JSONArray()
        for (w in widgets) {
            val obj = JSONObject().apply {
                put("displayName", w.displayName)
                put("packageName", w.packageName)
                put("customIconType", w.customIconType)
                put("customIconValue", w.customIconValue)
                put("backgroundColorHex", w.backgroundColorHex)
                put("textColorHex", w.textColorHex)
                put("shapeType", w.shapeType)
                put("badgeText", w.badgeText ?: "")
                put("createdAt", w.createdAt)
            }
            jsonArray.put(obj)
        }
        val wrapper = JSONObject().apply {
            put("version", 1)
            put("appName", "WidgetAppLauncher")
            put("exportedAt", System.currentTimeMillis())
            put("widgets", jsonArray)
        }
        return wrapper.toString(2)
    }

    fun importFromJson(jsonString: String): Result<List<CustomWidget>> {
        return try {
            val jsonStringTrimmed = jsonString.trim()
            val widgetsList = mutableListOf<CustomWidget>()

            val jsonArray = if (jsonStringTrimmed.startsWith("{")) {
                val wrapper = JSONObject(jsonStringTrimmed)
                wrapper.getJSONArray("widgets")
            } else if (jsonStringTrimmed.startsWith("[")) {
                JSONArray(jsonStringTrimmed)
            } else {
                return Result.failure(IllegalArgumentException("Format JSON tidak valid"))
            }

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val displayName = obj.optString("displayName", "Custom App")
                val packageName = obj.optString("packageName", "com.android.settings")
                val customIconType = obj.optString("customIconType", "SYSTEM_APP")
                val customIconValue = obj.optString("customIconValue", "")
                val backgroundColorHex = obj.optString("backgroundColorHex", "#1E293B")
                val textColorHex = obj.optString("textColorHex", "#FFFFFF")
                val shapeType = obj.optString("shapeType", "ROUNDED")
                val badgeText = obj.optString("badgeText", "").takeIf { it.isNotBlank() }

                widgetsList.add(
                    CustomWidget(
                        displayName = displayName,
                        packageName = packageName,
                        customIconType = customIconType,
                        customIconValue = customIconValue,
                        backgroundColorHex = backgroundColorHex,
                        textColorHex = textColorHex,
                        shapeType = shapeType,
                        badgeText = badgeText
                    )
                )
            }

            if (widgetsList.isEmpty()) {
                Result.failure(IllegalArgumentException("Tidak ada widget ditemukan dalam file backup"))
            } else {
                Result.success(widgetsList)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPresetSamples(): List<CustomWidget> {
        return listOf(
            CustomWidget(
                displayName = "WhatsApp",
                packageName = "com.whatsapp",
                customIconType = "PRESET_VECTOR",
                customIconValue = "chat",
                backgroundColorHex = "#10B981",
                textColorHex = "#FFFFFF",
                shapeType = "SQUIRCLE"
            ),
            CustomWidget(
                displayName = "YouTube",
                packageName = "com.google.android.youtube",
                customIconType = "PRESET_VECTOR",
                customIconValue = "video",
                backgroundColorHex = "#EF4444",
                textColorHex = "#FFFFFF",
                shapeType = "ROUNDED"
            ),
            CustomWidget(
                displayName = "Chrome",
                packageName = "com.android.chrome",
                customIconType = "PRESET_VECTOR",
                customIconValue = "browser",
                backgroundColorHex = "#3B82F6",
                textColorHex = "#FFFFFF",
                shapeType = "CIRCLE"
            ),
            CustomWidget(
                displayName = "Pengaturan",
                packageName = "com.android.settings",
                customIconType = "PRESET_VECTOR",
                customIconValue = "settings",
                backgroundColorHex = "#64748B",
                textColorHex = "#FFFFFF",
                shapeType = "GLASS"
            ),
            CustomWidget(
                displayName = "Play Store",
                packageName = "com.android.vending",
                customIconType = "PRESET_VECTOR",
                customIconValue = "store",
                backgroundColorHex = "#06B6D4",
                textColorHex = "#FFFFFF",
                shapeType = "SQUIRCLE"
            ),
            CustomWidget(
                displayName = "Kamera Kustom",
                packageName = "com.android.camera",
                customIconType = "PRESET_VECTOR",
                customIconValue = "camera",
                backgroundColorHex = "#8B5CF6",
                textColorHex = "#FFFFFF",
                shapeType = "GLASS"
            )
        )
    }
}
