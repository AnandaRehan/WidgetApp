package com.ehan.widgetapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.ehan.widgetapp.data.model.AppWidgetEntity

object WidgetManager {

    fun pinShortcutToHomeScreen(
        context: Context,
        widgetEntity: AppWidgetEntity,
        drawableIcon: Drawable?
    ): Boolean {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            return false
        }

        val launchIntent = context.packageManager.getLaunchIntentForPackage(widgetEntity.packageName)
            ?: Intent(Intent.ACTION_MAIN).apply {
                setPackage(widgetEntity.packageName)
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val bitmapIcon = createCustomBitmapIcon(context, widgetEntity, drawableIcon)
        val iconCompat = IconCompat.createWithBitmap(bitmapIcon)

        val shortcutInfo = ShortcutInfoCompat.Builder(context, "shortcut_${widgetEntity.id}_${System.currentTimeMillis()}")
            .setShortLabel(widgetEntity.customName)
            .setLongLabel(widgetEntity.customName)
            .setIcon(iconCompat)
            .setIntent(launchIntent)
            .build()

        return ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
    }

    fun createCustomBitmapIcon(
        context: Context,
        widgetEntity: AppWidgetEntity,
        drawableIcon: Drawable?
    ): Bitmap {
        val size = 192
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val colorInt = try {
            Color.parseColor(widgetEntity.iconColorHex)
        } catch (e: Exception) {
            Color.parseColor("#6366F1")
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rectF = RectF(0f, 0f, size.toFloat(), size.toFloat())

        // Draw background shape
        paint.color = colorInt
        when (widgetEntity.iconShape.uppercase()) {
            "CIRCLE" -> canvas.drawOval(rectF, paint)
            "ROUNDED_SQUARE" -> canvas.drawRoundRect(rectF, 24f, 24f, paint)
            else -> canvas.drawRoundRect(rectF, 48f, 48f, paint) // Squircle default
        }

        // Draw foreground content
        if (widgetEntity.iconType.uppercase() == "EMOJI") {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 96f
                textAlign = Paint.Align.CENTER
            }
            val fontMetrics = textPaint.fontMetrics
            val baseline = (size / 2f) - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
            canvas.drawText(widgetEntity.iconEmoji, size / 2f, baseline, textPaint)
        } else if (drawableIcon != null) {
            val iconSize = (size * 0.65).toInt()
            val left = (size - iconSize) / 2
            val top = (size - iconSize) / 2

            val iconBitmap = drawableToBitmap(drawableIcon, iconSize, iconSize)
            canvas.drawBitmap(iconBitmap, left.toFloat(), top.toFloat(), null)
        } else {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 80f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            val letter = widgetEntity.customName.firstOrNull()?.uppercase() ?: "A"
            val fontMetrics = textPaint.fontMetrics
            val baseline = (size / 2f) - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
            canvas.drawText(letter, size / 2f, baseline, textPaint)
        }

        return bitmap
    }

    private fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return Bitmap.createScaledBitmap(drawable.bitmap, width, height, true)
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }
}
