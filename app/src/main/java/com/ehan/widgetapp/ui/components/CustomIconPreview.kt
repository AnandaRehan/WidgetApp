package com.ehan.widgetapp.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(iconColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val shape = when (iconShape.uppercase()) {
        "CIRCLE" -> CircleShape
        "ROUNDED_SQUARE" -> RoundedCornerShape(12.dp)
        else -> RoundedCornerShape(20.dp) // Squircle style
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(if (iconType == "DEFAULT") MaterialTheme.colorScheme.surfaceVariant else parsedColor),
        contentAlignment = Alignment.Center
    ) {
        when (iconType.uppercase()) {
            "EMOJI" -> {
                Text(
                    text = iconEmoji,
                    fontSize = (size.value * 0.55).sp
                )
            }
            "COLOR" -> {
                if (appDrawable != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = appDrawable),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(size * 0.65f)
                    )
                } else {
                    Text(
                        text = fallbackName.firstOrNull()?.uppercase() ?: "A",
                        color = Color.White,
                        fontSize = (size.value * 0.45).sp,
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
                        modifier = Modifier.size(size * 0.75f)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "App Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(size * 0.65f)
                    )
                }
            }
        }
    }
}
