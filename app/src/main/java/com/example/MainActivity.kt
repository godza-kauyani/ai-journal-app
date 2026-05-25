package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ui.TaskBoardScreen
import com.example.ui.TaskViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge drawing capabilities enabled
        enableEdgeToEdge()
        
        // Instantiate the TaskViewModel using our repository provider factory
        val viewModel = ViewModelProvider(
            this,
            TaskViewModel.provideFactory(this)
        )[TaskViewModel::class.java]
        
        setContent {
            MyApplicationTheme {
                TaskBoardScreen(viewModel = viewModel)
            }
        }
    }
}
