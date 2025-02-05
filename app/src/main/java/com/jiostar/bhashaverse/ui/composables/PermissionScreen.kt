package com.jiostar.bhashaverse.ui.composables

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun PermissionScreen(
    permission: String,
    rationale: String,
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
) {
    var hasPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            onPermissionGranted()
        }
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            launcher.launch(permission)
        }
    }

    if (hasPermission) {
        content()
    } else {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Permission Required") },
            text = { Text(text = rationale) },
            confirmButton = {
                Button(
                    onClick = {
                        launcher.launch(permission)
                    }
                ) {
                    Text(text = "Grant Permission")
                }
            }
        )
    }
}