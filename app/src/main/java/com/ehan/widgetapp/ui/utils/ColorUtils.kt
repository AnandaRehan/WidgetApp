package com.ehan.widgetapp.ui.utils

import android.graphics.Color
import androidx.core.graphics.ColorUtils as AndroidColorUtils

object ColorUtils {

    fun hexToRgb(hex: String): Triple<Int, Int, Int>? {
        return try {
            val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
            val colorInt = Color.parseColor(cleanHex)
            Triple(Color.red(colorInt), Color.green(colorInt), Color.blue(colorInt))
        } catch (e: Exception) {
            null
        }
    }

    fun hexToHsl(hex: String): Triple<Float, Float, Float>? {
        return try {
            val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
            val colorInt = Color.parseColor(cleanHex)
            val hsl = FloatArray(3)
            AndroidColorUtils.colorToHSL(colorInt, hsl)
            Triple(hsl[0], hsl[1] * 100f, hsl[2] * 100f)
        } catch (e: Exception) {
            null
        }
    }

    fun rgbToHex(r: Int, g: Int, b: Int): String {
        val cr = r.coerceIn(0, 255)
        val cg = g.coerceIn(0, 255)
        val cb = b.coerceIn(0, 255)
        return String.format("#%02X%02X%02X", cr, cg, cb)
    }

    fun hslToHex(h: Float, s: Float, l: Float): String {
        val ch = h.coerceIn(0f, 360f)
        val cs = (s / 100f).coerceIn(0f, 1f)
        val cl = (l / 100f).coerceIn(0f, 1f)
        val colorInt = AndroidColorUtils.HSLToColor(floatArrayOf(ch, cs, cl))
        return String.format("#%02X%02X%02X", Color.red(colorInt), Color.green(colorInt), Color.blue(colorInt))
    }
}
