package com.example.ui.screens

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShapeLine
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CustomWidget
import com.example.data.model.InstalledApp
import com.example.ui.viewmodel.WidgetViewModel
import com.example.utils.AppListManager
import com.example.widget.WidgetHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWidgetDialog(
    viewModel: WidgetViewModel,
    editingWidget: CustomWidget? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }

    val installedApps by viewModel.filteredInstalledApps.collectAsState()
    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val appSearchQuery by viewModel.appSearchQuery.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: From Installed Apps, 1: Custom Package Name
    var displayName by remember { mutableStateOf(editingWidget?.displayName ?: "") }
    var packageName by remember { mutableStateOf(editingWidget?.packageName ?: "") }
    var customIconType by remember { mutableStateOf(editingWidget?.customIconType ?: "SYSTEM_APP") }
    var customIconValue by remember { mutableStateOf(editingWidget?.customIconValue ?: "chat") }
    var backgroundColorHex by remember { mutableStateOf(editingWidget?.backgroundColorHex ?: "#1E293B") }
    var textColorHex by remember { mutableStateOf(editingWidget?.textColorHex ?: "#FFFFFF") }
    var shapeType by remember { mutableStateOf(editingWidget?.shapeType ?: "ROUNDED") }

    val presetIcons = remember {
        listOf("chat", "video", "browser", "settings", "camera", "store", "game", "music", "social", "star", "rocket", "flame", "shield", "code")
    }

    val colorOptions = remember {
        listOf("#1E293B", "#6366F1", "#10B981", "#EF4444", "#F59E0B", "#8B5CF6", "#06B6D4", "#EC4899", "#000000", "#FFFFFF")
    }

    val shapeOptions = remember {
        listOf("ROUNDED" to "Persegi", "CIRCLE" to "Lingkaran", "SQUIRCLE" to "Squircle", "GLASS" to "Kaca")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingWidget == null) "Buat Widget Aplikasi" else "Edit Widget Aplikasi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Widget Preview Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PREVIEW WIDGET",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preview Box
                        val sampleWidget = CustomWidget(
                            displayName = if (displayName.isBlank()) "Nama App" else displayName,
                            packageName = if (packageName.isBlank()) "com.example.app" else packageName,
                            customIconType = customIconType,
                            customIconValue = customIconValue,
                            backgroundColorHex = backgroundColorHex,
                            textColorHex = textColorHex,
                            shapeType = shapeType
                        )

                        val iconBitmap = remember(sampleWidget) {
                            WidgetHelper.getWidgetIconBitmap(context, sampleWidget)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .width(90.dp)
                                .padding(8.dp)
                        ) {
                            if (iconBitmap != null) {
                                Image(
                                    bitmap = iconBitmap.asImageBitmap(),
                                    contentDescription = "Preview Icon",
                                    modifier = Modifier.size(56.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color.Gray, CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (displayName.isBlank()) "Aplikasi" else displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = try {
                                    Color(android.graphics.Color.parseColor(textColorHex))
                                } catch (e: Exception) {
                                    Color.White
                                },
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // App Selection Tabs (Daftar Aplikasi vs Input Nama Paket Custom)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Daftar App", fontSize = 12.sp)
                        }
                    }
                    SegmentedButton(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paket Custom", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedTab == 0) {
                        // TAB 0: INSTALLED APPS LIST
                        item {
                            OutlinedTextField(
                                value = appSearchQuery,
                                onValueChange = { viewModel.setAppSearchQuery(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_installed_apps_input"),
                                placeholder = { Text("Cari nama aplikasi atau nama paket...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        if (isLoadingApps) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        } else if (installedApps.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Aplikasi tidak ditemukan dalam pencarian.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(installedApps.take(30)) { app ->
                                InstalledAppRow(
                                    app = app,
                                    isSelected = packageName == app.packageName,
                                    onSelect = {
                                        packageName = app.packageName
                                        if (displayName.isBlank() || editingWidget == null) {
                                            displayName = app.label
                                        }
                                        customIconType = "SYSTEM_APP"
                                    }
                                )
                            }
                        }
                    } else {
                        // TAB 1: CUSTOM PACKAGE NAME INPUT
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "💡 Masukkan Nama Paket Aplikasi",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Fitur ini berguna jika aplikasi tidak ada di daftar, tersembunyi, atau aplikasi kustom khusus.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = packageName,
                                onValueChange = { packageName = it },
                                label = { Text("Nama Paket (Package Name)") },
                                placeholder = { Text("misal: com.whatsapp atau com.android.chrome") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_package_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // CUSTOMIZATION SECTION
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "Kustomisasi Nama & Ikon",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Nama Tampilan Widget") },
                            placeholder = { Text("misal: Chat Saya") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_display_name_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        Text(
                            text = "Jenis Ikon:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = customIconType == "SYSTEM_APP",
                                onClick = { customIconType = "SYSTEM_APP" },
                                label = { Text("Ikon Asli App") }
                            )
                            FilterChip(
                                selected = customIconType == "PRESET_VECTOR",
                                onClick = { customIconType = "PRESET_VECTOR" },
                                label = { Text("Ikon Simbol Preset") }
                            )
                        }

                        if (customIconType == "PRESET_VECTOR") {
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(presetIcons) { iconKey ->
                                    val symbol = WidgetHelper.getSymbolForPreset(iconKey)
                                    val isSel = customIconValue == iconKey
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { customIconValue = iconKey },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = symbol, fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }

                    // WIDGET SHAPE
                    item {
                        Text(
                            text = "Bentuk Widget:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(shapeOptions) { (key, label) ->
                                FilterChip(
                                    selected = shapeType == key,
                                    onClick = { shapeType = key },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }

                    // BACKGROUND COLOR
                    item {
                        Text(
                            text = "Warna Latar Belakang:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(colorOptions) { hex ->
                                val isSel = backgroundColorHex.equals(hex, ignoreCase = true)
                                val color = try {
                                    Color(android.graphics.Color.parseColor(hex))
                                } catch (e: Exception) {
                                    Color.Gray
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSel) 3.dp else 1.dp,
                                            color = if (isSel) MaterialTheme.colorScheme.primary else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .clickable { backgroundColorHex = hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSel) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SAVE BUTTON
                Button(
                    onClick = {
                        if (packageName.isBlank()) {
                            Toast.makeText(context, "Silakan pilih aplikasi atau masukkan nama paket", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val finalDisplayName = if (displayName.isBlank()) packageName else displayName

                        val widgetToSave = CustomWidget(
                            id = editingWidget?.id ?: 0L,
                            displayName = finalDisplayName,
                            packageName = packageName.trim(),
                            customIconType = customIconType,
                            customIconValue = customIconValue,
                            backgroundColorHex = backgroundColorHex,
                            textColorHex = textColorHex,
                            shapeType = shapeType
                        )

                        viewModel.saveWidget(widgetToSave) { savedId ->
                            val updatedWidget = widgetToSave.copy(id = savedId)
                            viewModel.pinWidgetToHomeScreen(context, updatedWidget)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_widget_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan & Pasang Widget", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InstalledAppRow(
    app: InstalledApp,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val bitmap = remember(app.icon) {
                app.icon?.let { AppListManager.drawableToBitmap(it) }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
