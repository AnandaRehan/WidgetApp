package com.ehan.widgetapp.ui.screens

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ehan.widgetapp.data.model.InstalledAppInfo
import com.ehan.widgetapp.ui.components.CustomIconPreview

val COLOR_PRESETS = listOf(
    "#4F46E5", // Indigo
    "#0EA5E9", // Sky
    "#10B981", // Emerald
    "#F59E0B", // Amber
    "#EF4444", // Rose
    "#8B5CF6", // Violet
    "#EC4899", // Pink
    "#0F172A"  // Slate
)

val EMOJI_PRESETS = listOf("📱", "🎮", "💬", "🎵", "📷", "🛒", "💼", "⚡", "🚀", "❤️", "⭐", "🧭", "🔐", "🎨")

val SHAPE_PRESETS = listOf(
    "SQUIRCLE" to "Squircle",
    "CIRCLE" to "Lingkaran",
    "ROUNDED_SQUARE" to "Persegi"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetCustomizeDialog(
    appInfo: InstalledAppInfo,
    customName: String,
    onCustomNameChanged: (String) -> Unit,
    iconType: String,
    onIconTypeChanged: (String) -> Unit,
    iconColorHex: String,
    onIconColorChanged: (String) -> Unit,
    iconEmoji: String,
    onIconEmojiChanged: (String) -> Unit,
    iconShape: String,
    onIconShapeChanged: (String) -> Unit,
    onSave: () -> Unit,
    onSaveAndPin: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kustomisasi Widget Pemintas",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_customize_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconPreview(
                        iconType = iconType,
                        iconColorHex = iconColorHex,
                        iconEmoji = iconEmoji,
                        iconShape = iconShape,
                        appDrawable = appInfo.icon,
                        fallbackName = customName.ifBlank { appInfo.appName },
                        size = 64.dp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = customName.ifBlank { appInfo.appName },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = appInfo.packageName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom Name Field
            OutlinedTextField(
                value = customName,
                onValueChange = onCustomNameChanged,
                label = { Text("Nama Pemintas Custom") },
                placeholder = { Text(appInfo.appName) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_name_input_field")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Icon Type Selector
            Text(
                text = "Tipe Ikon Widget",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = iconType == "DEFAULT",
                    onClick = { onIconTypeChanged("DEFAULT") },
                    label = { Text("Default") },
                    modifier = Modifier.testTag("chip_icon_default")
                )
                FilterChip(
                    selected = iconType == "COLOR",
                    onClick = { onIconTypeChanged("COLOR") },
                    label = { Text("Warna Badge") },
                    modifier = Modifier.testTag("chip_icon_color")
                )
                FilterChip(
                    selected = iconType == "EMOJI",
                    onClick = { onIconTypeChanged("EMOJI") },
                    label = { Text("Emoji") },
                    modifier = Modifier.testTag("chip_icon_emoji")
                )
            }

            // Options based on iconType
            if (iconType == "COLOR" || iconType == "EMOJI") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Warna Latar Ikon",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(COLOR_PRESETS) { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = iconColorHex.equals(hex, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onIconColorChanged(hex) }
                        )
                    }
                }
            }

            if (iconType == "EMOJI") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pilih Simbol Emoji",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(EMOJI_PRESETS) { emoji ->
                        val isSelected = iconEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { onIconEmojiChanged(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bentuk Ikon",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SHAPE_PRESETS.forEach { (key, label) ->
                    FilterChip(
                        selected = iconShape.equals(key, ignoreCase = true),
                        onClick = { onIconShapeChanged(key) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSaveAndPin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_and_pin_btn"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Simpan & Pasang ke Layar Utama")
                }

                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_only_btn"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Simpan ke WidgetApp Saja")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
