package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainAppContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Secure screen protocol: Emulate fintech privacy standard by blocking screenshot capture
    // Note: Commented out in development so the AI Studio web streaming preview is not black.
    // Feel free to uncomment this for production release builds to enforce physical screen security!
    // window.setFlags(
    //   WindowManager.LayoutParams.FLAG_SECURE,
    //   WindowManager.LayoutParams.FLAG_SECURE
    // )

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContainer(viewModel = viewModel)
      }
    }
  }
}
