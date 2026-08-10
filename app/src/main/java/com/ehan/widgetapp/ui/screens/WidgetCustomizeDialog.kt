package com.ehan.widgetapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ehan.widgetapp.data.model.InstalledAppInfo
import com.ehan.widgetapp.ui.components.CustomIconPreview
import com.ehan.widgetapp.ui.utils.ColorUtils

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

val EMOJI_PRESETS = listOf("📱", "🎮", "💬", "🎵", "📷", "🛒", "💼", "⚡", "🚀", "❤️", "⭐", "🧭", "🔐", "🎨", "🔥", "🌐")

val SHAPE_PRESETS = listOf(
    "SQUIRCLE" to "Squircle",
    "CIRCLE" to "Lingkaran",
    "ROUNDED_SQUARE" to "Persegi",
    "FULL" to "Penuh (Full)"
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
    customImageUri: String?,
    onPickGalleryImage: (Uri) -> Unit,
    isTransparentBg: Boolean,
    onIsTransparentBgChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    onSaveAndPin: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val scrollState = rememberScrollState()

    // Color Tab Mode: 0 = Preset, 1 = Transparan, 2 = HEX, 3 = RGB, 4 = HSL
    var colorTabMode by remember(isTransparentBg) {
        mutableStateOf(if (isTransparentBg) 1 else 0)
    }

    // Manual input fields
    var hexInput by remember(iconColorHex) { mutableStateOf(iconColorHex) }
    var rInput by remember { mutableStateOf("79") }
    var gInput by remember { mutableStateOf("70") }
    var bInput by remember { mutableStateOf("229") }
    var hInput by remember { mutableStateOf("243") }
    var sInput by remember { mutableStateOf("75") }
    var lInput by remember { mutableStateOf("59") }

    // Sync manual inputs when iconColorHex changes
    LaunchedEffect(iconColorHex) {
        val rgb = ColorUtils.hexToRgb(iconColorHex)
        if (rgb != null) {
            rInput = rgb.first.toString()
            gInput = rgb.second.toString()
            bInput = rgb.third.toString()
        }
        val hsl = ColorUtils.hexToHsl(iconColorHex)
        if (hsl != null) {
            hInput = hsl.first.toInt().toString()
            sInput = hsl.second.toInt().toString()
            lInput = hsl.third.toInt().toString()
        }
    }

    // Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onPickGalleryImage(it)
            onIconTypeChanged("GALLERY")
        }
    }

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
                        customImageUri = customImageUri,
                        isTransparentBg = isTransparentBg,
                        size = 68.dp
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

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = iconType == "DEFAULT",
                        onClick = { onIconTypeChanged("DEFAULT") },
                        label = { Text("Default") },
                        modifier = Modifier.testTag("chip_icon_default")
                    )
                }
                item {
                    FilterChip(
                        selected = iconType == "COLOR",
                        onClick = { onIconTypeChanged("COLOR") },
                        label = { Text("Warna Badge") },
                        modifier = Modifier.testTag("chip_icon_color")
                    )
                }
                item {
                    FilterChip(
                        selected = iconType == "EMOJI",
                        onClick = { onIconTypeChanged("EMOJI") },
                        label = { Text("Emoji") },
                        modifier = Modifier.testTag("chip_icon_emoji")
                    )
                }
                item {
                    FilterChip(
                        selected = iconType == "GALLERY",
                        onClick = {
                            onIconTypeChanged("GALLERY")
                            if (customImageUri.isNullOrEmpty()) {
                                galleryLauncher.launch("image/*")
                            }
                        },
                        label = { Text("Foto Galeri") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("chip_icon_gallery")
                    )
                }
            }

            // Gallery Picker Button if GALLERY selected
            if (iconType == "GALLERY") {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!customImageUri.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = customImageUri),
                                    contentDescription = "Thumbnail Galeri",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Foto galeri dipilih",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Belum ada foto dipilih",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Pilih Foto")
                        }
                    }
                }
            }

            // Pemilihan Background & Warna Latar
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pemilihan Background",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                // Color Preview Box (if not transparent)
                if (!isTransparentBg) {
                    val currentColor = try {
                        Color(android.graphics.Color.parseColor(iconColorHex))
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.primary
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(currentColor)
                            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Color & Background Tabs: Preset | Transparan | HEX | RGB | HSL
            val tabs = listOf("Preset", "Transparan", "HEX", "RGB", "HSL")
            TabRow(
                selectedTabIndex = colorTabMode,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = colorTabMode == index,
                        onClick = {
                            colorTabMode = index
                            if (index == 1) {
                                onIsTransparentBgChanged(true)
                            } else {
                                onIsTransparentBgChanged(false)
                            }
                        },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (colorTabMode) {
                0 -> { // Presets
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(COLOR_PRESETS) { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = !isTransparentBg && iconColorHex.equals(hex, ignoreCase = true)

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onIsTransparentBgChanged(false)
                                        onIconColorChanged(hex)
                                    }
                            )
                        }
                    }
                }
                1 -> { // Transparan Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Background Transparan Aktif",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ikon, emoji, atau foto galeri akan ditampilkan polos tanpa kotak/lingkaran warna latar.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                2 -> { // HEX Input
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hexInput,
                            onValueChange = { input ->
                                hexInput = input
                                val formatted = if (input.startsWith("#")) input else "#$input"
                                if (formatted.length == 7 || formatted.length == 9) {
                                    try {
                                        android.graphics.Color.parseColor(formatted)
                                        onIsTransparentBgChanged(false)
                                        onIconColorChanged(formatted)
                                    } catch (_: Exception) {}
                                }
                            },
                            label = { Text("Kode HEX (Contoh: #4F46E5)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                3 -> { // RGB Input
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = rInput,
                            onValueChange = { input ->
                                rInput = input
                                val r = input.toIntOrNull() ?: 0
                                val g = gInput.toIntOrNull() ?: 0
                                val b = bInput.toIntOrNull() ?: 0
                                onIsTransparentBgChanged(false)
                                onIconColorChanged(ColorUtils.rgbToHex(r, g, b))
                            },
                            label = { Text("Red (R)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = gInput,
                            onValueChange = { input ->
                                gInput = input
                                val r = rInput.toIntOrNull() ?: 0
                                val g = input.toIntOrNull() ?: 0
                                val b = bInput.toIntOrNull() ?: 0
                                onIsTransparentBgChanged(false)
                                onIconColorChanged(ColorUtils.rgbToHex(r, g, b))
                            },
                            label = { Text("Green (G)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bInput,
                            onValueChange = { input ->
                                bInput = input
                                val r = rInput.toIntOrNull() ?: 0
                                val g = gInput.toIntOrNull() ?: 0
                                val b = input.toIntOrNull() ?: 0
                                onIsTransparentBgChanged(false)
                                onIconColorChanged(ColorUtils.rgbToHex(r, g, b))
                            },
                            label = { Text("Blue (B)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                4 -> { // HSL Input
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = hInput,
                            onValueChange = { input ->
                                hInput = input
                                val h = input.toFloatOrNull() ?: 0f
                                val s = sInput.toFloatOrNull() ?: 0f
                                val l = lInput.toFloatOrNull() ?: 0f
                                onIsTransparentBgChanged(false)
                                onIconColorChanged(ColorUtils.hslToHex(h, s, l))
                            },
                            label = { Text("Hue (°)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sInput,
                            onValueChange = { input ->
                                sInput = input
                                val h = hInput.toFloatOrNull() ?: 0f
                                val s = input.toFloatOrNull() ?: 0f
                                val l = lInput.toFloatOrNull() ?: 0f
                                onIsTransparentBgChanged(false)
                                onIconColorChanged(ColorUtils.hslToHex(h, s, l))
                            },
                            label = { Text("Sat (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lInput,
                            onValueChange = { input ->
                                lInput = input
                                val h = hInput.toFloatOrNull() ?: 0f
                                val s = sInput.toFloatOrNull() ?: 0f
                                val l = input.toFloatOrNull() ?: 0f
                                onIsTransparentBgChanged(false)
                                onIconColorChanged(ColorUtils.hslToHex(h, s, l))
                            },
                            label = { Text("Light (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
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

            if (!isTransparentBg) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bentuk Ikon Latar",
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
