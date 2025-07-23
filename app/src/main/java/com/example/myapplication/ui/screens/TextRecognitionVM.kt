package com.example.myapplication.ui.screens

import android.app.Application
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TextRecognitionVM(application: Application) : AndroidViewModel(application) {
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    fun recognizeTextFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    _recognizedText.value = result.text
                }
                .addOnFailureListener {
                    _recognizedText.value = "Erro ao reconhecer texto."
                }
        }
    }
}
