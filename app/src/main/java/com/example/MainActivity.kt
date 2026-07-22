package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.db.AppDatabase
import com.example.data.repository.WidgetRepository
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WidgetViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WidgetViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = WidgetRepository(database.customWidgetDao())
        WidgetViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
