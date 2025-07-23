package com.example.myapplication.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MovementDemoScreen(viewModel: MovementViewModel = viewModel()) {
    val isMoving by viewModel.isMoving.collectAsState()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        viewModel.start()
        onDispose {
            viewModel.stop()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isMoving) "Você está em movimento!" else "Você está parado.",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Demonstração de sensibilidade ao movimento (acelerômetro)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
