package com.ehan.widgetapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.ehan.widgetapp.data.model.AppWidgetEntity
import java.io.File

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

        // Use adaptive bitmap on API 26+ if not transparent bg, so launcher fills edge-to-edge without extra padding
        val iconCompat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !widgetEntity.isTransparentBg) {
            IconCompat.createWithAdaptiveBitmap(bitmapIcon)
        } else {
            IconCompat.createWithBitmap(bitmapIcon)
        }

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
        val size = 512
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val isTransparent = widgetEntity.isTransparentBg || widgetEntity.iconType.equals("NO_BG", ignoreCase = true)
        val isFullShape = widgetEntity.iconShape.equals("FULL", ignoreCase = true)

        val colorInt = try {
            Color.parseColor(widgetEntity.iconColorHex)
        } catch (e: Exception) {
            Color.parseColor("#6366F1")
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rectF = RectF(0f, 0f, size.toFloat(), size.toFloat())

        if (!isTransparent) {
            paint.color = colorInt
            when (widgetEntity.iconShape.uppercase()) {
                "CIRCLE" -> canvas.drawOval(rectF, paint)
                "ROUNDED_SQUARE" -> canvas.drawRoundRect(rectF, 96f, 96f, paint)
                "SQUIRCLE" -> canvas.drawRoundRect(rectF, 140f, 140f, paint)
                else -> canvas.drawRect(rectF, paint) // Full edge-to-edge solid fill
            }
        }

        val type = widgetEntity.iconType.uppercase()

        if (type == "GALLERY" && !widgetEntity.customImageUri.isNullOrEmpty()) {
            val galleryBitmap = loadBitmapFromUriOrPath(context, widgetEntity.customImageUri)
            if (galleryBitmap != null) {
                val padding = if (isTransparent || isFullShape) 0 else (size * 0.12).toInt()
                val targetSize = size - (padding * 2)
                val scaled = Bitmap.createScaledBitmap(galleryBitmap, targetSize, targetSize, true)
                canvas.drawBitmap(scaled, padding.toFloat(), padding.toFloat(), null)
                return bitmap
            }
        }

        if (type == "EMOJI") {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = if (isFullShape) 420f else if (isTransparent) 340f else 260f
                textAlign = Paint.Align.CENTER
            }
            val fontMetrics = textPaint.fontMetrics
            val baseline = (size / 2f) - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
            canvas.drawText(widgetEntity.iconEmoji, size / 2f, baseline, textPaint)
        } else if (drawableIcon != null) {
            val iconScale = if (isFullShape) 1.0f else if (isTransparent) 0.88f else 0.65f
            val iconSize = (size * iconScale).toInt()
            val left = (size - iconSize) / 2
            val top = (size - iconSize) / 2

            val iconBitmap = drawableToBitmap(drawableIcon, iconSize, iconSize)
            canvas.drawBitmap(iconBitmap, left.toFloat(), top.toFloat(), null)
        } else {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (isTransparent) colorInt else Color.WHITE
                textSize = if (isFullShape) 320f else 220f
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

    private fun loadBitmapFromUriOrPath(context: Context, pathOrUri: String): Bitmap? {
        return try {
            if (pathOrUri.startsWith("/")) {
                val file = File(pathOrUri)
                if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
            } else {
                val uri = Uri.parse(pathOrUri)
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        } catch (e: Exception) {
            null
        }
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
