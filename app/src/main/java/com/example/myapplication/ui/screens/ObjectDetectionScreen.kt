package com.example.myapplication.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.InputStream

@Composable
fun ObjectDetectionScreen(viewModel: ObjectDetectionViewModel = viewModel()) {
    val context = LocalContext.current
    val objects by viewModel.objects.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bmp = BitmapFactory.decodeStream(inputStream)
            bitmap = bmp
            if (bmp != null) {
                viewModel.detectObjectsFromBitmap(bmp)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Selecionar Imagem")
        }
        Spacer(modifier = Modifier.height(16.dp))
        bitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text("Objetos reconhecidos:", style = MaterialTheme.typography.titleMedium)
        objects.forEach {
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
