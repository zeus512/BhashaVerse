package com.jiostar.bhashaverse.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jiostar.bhashaverse.ui.composables.MainScreen
import com.jiostar.bhashaverse.ui.composables.PermissionScreen
import com.jiostar.bhashaverse.ui.theme.BhashaVerseTheme
import com.jiostar.bhashaverse.ui.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BhashaVerseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        PermissionScreen(
                            permission = Manifest.permission.POST_NOTIFICATIONS,
                            rationale = "Notifications are needed to show you network traffic.",
                            onPermissionGranted = {
                                // Do something when permission is granted, if needed
                            }
                        ) {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}

