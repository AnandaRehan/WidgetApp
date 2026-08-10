package com.ehan.widgetapp.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun CustomIconPreview(
    iconType: String,
    iconColorHex: String,
    iconEmoji: String,
    iconShape: String,
    appDrawable: Drawable?,
    fallbackName: String,
    customImageUri: String? = null,
    isTransparentBg: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val isTransparent = isTransparentBg || iconType.equals("NO_BG", ignoreCase = true)
    val isFullShape = iconShape.equals("FULL", ignoreCase = true)

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(iconColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val shape = when (iconShape.uppercase()) {
        "CIRCLE" -> CircleShape
        "ROUNDED_SQUARE" -> RoundedCornerShape(12.dp)
        "FULL" -> RectangleShape
        else -> RoundedCornerShape(20.dp) // Squircle style
    }

    val bgModifier = if (isTransparent) {
        Modifier.background(Color.Transparent)
    } else if (iconType.equals("DEFAULT", ignoreCase = true)) {
        Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
    } else {
        Modifier.background(parsedColor)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .then(bgModifier),
        contentAlignment = Alignment.Center
    ) {
        when (iconType.uppercase()) {
            "GALLERY" -> {
                if (!customImageUri.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = customImageUri),
                        contentDescription = "Custom Icon Gallery",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Pilih Galeri",
                        tint = if (isTransparent) MaterialTheme.colorScheme.primary else Color.White,
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
            }
            "EMOJI" -> {
                Text(
                    text = iconEmoji,
                    fontSize = (size.value * if (isFullShape) 0.82f else if (isTransparent) 0.68f else 0.55f).sp
                )
            }
            "COLOR" -> {
                if (appDrawable != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = appDrawable),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(size * if (isFullShape) 1.0f else if (isTransparent) 0.85f else 0.65f)
                    )
                } else {
                    Text(
                        text = fallbackName.firstOrNull()?.uppercase() ?: "A",
                        color = if (isTransparent) parsedColor else Color.White,
                        fontSize = (size.value * if (isFullShape) 0.65f else 0.45f).sp,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            else -> {
                // DEFAULT
                if (appDrawable != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = appDrawable),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(size * if (isFullShape) 1.0f else if (isTransparent) 0.9f else 0.75f)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "App Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(size * if (isFullShape) 0.9f else 0.65f)
                    )
                }
            }
        }
    }
}
