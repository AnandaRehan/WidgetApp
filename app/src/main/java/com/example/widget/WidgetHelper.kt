package com.example.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.data.model.CustomWidget
import com.example.utils.AppListManager

object WidgetHelper {

    fun getWidgetIconBitmap(context: Context, widget: CustomWidget): Bitmap? {
        val size = 192 // High res bitmap for homescreen widget

        return try {
            when (widget.customIconType) {
                "SYSTEM_APP" -> {
                    val pm = context.packageManager
                    val iconDrawable = try {
                        pm.getApplicationIcon(widget.packageName)
                    } catch (e: Exception) {
                        pm.defaultActivityIcon
                    }
                    val rawBitmap = AppListManager.drawableToBitmap(iconDrawable)
                    applyShapeToBitmap(rawBitmap, widget.shapeType, widget.backgroundColorHex)
                }

                "PRESET_VECTOR" -> {
                    drawPresetIconBitmap(widget.customIconValue, widget.backgroundColorHex, widget.textColorHex, widget.shapeType)
                }

                else -> {
                    // COLORED_BADGE or Default
                    val pm = context.packageManager
                    val iconDrawable = try {
                        pm.getApplicationIcon(widget.packageName)
                    } catch (e: Exception) {
                        pm.defaultActivityIcon
                    }
                    val rawBitmap = AppListManager.drawableToBitmap(iconDrawable)
                    applyShapeToBitmap(rawBitmap, widget.shapeType, widget.backgroundColorHex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun applyShapeToBitmap(srcBitmap: Bitmap, shapeType: String, bgColorHex: String): Bitmap {
        val size = 192
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())

        // Draw Background Shape
        try {
            paint.color = Color.parseColor(bgColorHex)
        } catch (e: Exception) {
            paint.color = Color.parseColor("#1E293B")
        }

        val rx = when (shapeType) {
            "CIRCLE" -> size / 2f
            "ROUNDED" -> size * 0.25f
            "SQUIRCLE" -> size * 0.35f
            "GLASS" -> size * 0.20f
            else -> size * 0.25f
        }

        canvas.drawRoundRect(rect, rx, rx, paint)

        // Draw inner scaled srcBitmap
        val margin = (size * 0.15f).toInt()
        val destRect = Rect(margin, margin, size - margin, size - margin)
        canvas.drawBitmap(srcBitmap, null, destRect, null)

        return output
    }

    private fun drawPresetIconBitmap(
        iconName: String,
        bgColorHex: String,
        fgColorHex: String,
        shapeType: String
    ): Bitmap {
        val size = 192
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())

        // Draw Background
        try {
            paint.color = Color.parseColor(bgColorHex)
        } catch (e: Exception) {
            paint.color = Color.parseColor("#6366F1")
        }

        val rx = when (shapeType) {
            "CIRCLE" -> size / 2f
            "ROUNDED" -> size * 0.25f
            "SQUIRCLE" -> size * 0.35f
            "GLASS" -> size * 0.20f
            else -> size * 0.25f
        }
        canvas.drawRoundRect(rect, rx, rx, paint)

        // Draw Symbol Text or Initial Letter in Foreground Color
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            try {
                color = Color.parseColor(fgColorHex)
            } catch (e: Exception) {
                color = Color.WHITE
            }
            textSize = size * 0.45f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val symbol = getSymbolForPreset(iconName)
        val yPos = (canvas.height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(symbol, size / 2f, yPos, textPaint)

        return output
    }

    fun getSymbolForPreset(presetKey: String): String {
        return when (presetKey.lowercase()) {
            "chat", "whatsapp", "messages" -> "💬"
            "video", "youtube", "media" -> "▶"
            "browser", "chrome", "web" -> "🌐"
            "settings", "pengaturan", "gear" -> "⚙"
            "camera", "kamera", "photo" -> "📷"
            "store", "playstore", "shop" -> "🛍"
            "game", "gaming" -> "🎮"
            "music", "audio", "spotify" -> "🎵"
            "social", "instagram", "heart" -> "♥"
            "mail", "email", "gmail" -> "✉"
            "star", "favorite" -> "★"
            "code", "developer", "terminal" -> "💻"
            "rocket", "fast", "launch" -> "🚀"
            "flame", "hot", "trending" -> "🔥"
            "shield", "security", "lock" -> "🛡"
            else -> "⚡"
        }
    }
}
