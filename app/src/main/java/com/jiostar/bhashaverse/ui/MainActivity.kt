package com.jiostar.bhashaverse.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import com.jiostar.bhashaverse.ui.composables.MainScreen
import com.jiostar.bhashaverse.ui.composables.MainScreenWithManifest
import com.jiostar.bhashaverse.ui.composables.PermissionScreen
import com.jiostar.bhashaverse.ui.theme.BhashaVerseTheme
import com.jiostar.bhashaverse.ui.utils.LocalImageLoader
import com.jiostar.bhashaverse.ui.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BhashaVerseTheme {
                CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            PermissionScreen(
                                permission = Manifest.permission.POST_NOTIFICATIONS,
                                rationale = "Notifications are needed to show you network traffic.",
                                onPermissionGranted = {
                                    // Do something when permission is granted, if needed
                                }
                            ) {
                                var regularFlowEnabled by remember { mutableStateOf(false) }
                                var manifestFlowEnabled by remember { mutableStateOf(false) }
                                if (regularFlowEnabled.not() && manifestFlowEnabled.not()) {
                                    Column(
                                        Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Button(onClick = {
                                            regularFlowEnabled = true
                                        }) {
                                            Text("Regular Flow")
                                        }
                                        Button(onClick = {
                                            manifestFlowEnabled = true
                                        }) {
                                            Text("Manifest Flow")
                                        }
                                    }
                                }
                                if (manifestFlowEnabled) {
                                    MainScreenWithManifest()
                                }
                                if (regularFlowEnabled) {
                                    MainScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

