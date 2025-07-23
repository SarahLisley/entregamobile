package com.example.myapplication.ui.screens

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ObjectDetectionViewModel(application: Application) : AndroidViewModel(application) {
    private val _objects = MutableStateFlow<List<String>>(emptyList())
    val objects: StateFlow<List<String>> = _objects

    fun detectObjectsFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            val image = InputImage.fromBitmap(bitmap, 0)
            val options = ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification() // Habilita classificação (ex: "Food", "Cat", etc)
                .build()
            val detector = ObjectDetection.getClient(options)
            detector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    val results = detectedObjects.map { obj ->
                        val label = obj.labels.firstOrNull()?.text ?: "Objeto"
                        val conf = obj.labels.firstOrNull()?.confidence?.let { " (${(it * 100).toInt()}%)" } ?: ""
                        "$label$conf"
                    }
                    _objects.value = results.ifEmpty { listOf("Nenhum objeto reconhecido.") }
                }
                .addOnFailureListener {
                    _objects.value = listOf("Erro ao reconhecer objetos.")
                }
        }
    }
}
