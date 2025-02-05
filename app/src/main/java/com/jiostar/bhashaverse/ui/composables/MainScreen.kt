package com.jiostar.bhashaverse.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jiostar.bhashaverse.ui.viewmodels.MainActivityViewModel

@Composable
fun MainScreen(viewModel: MainActivityViewModel = hiltViewModel()) {
    val state by viewModel.mainScreenState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }
    Column(
        Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        if (state.errorMessage.isNotEmpty()) {
            // Show error message
            Text(text = state.errorMessage)
        }
    }

}