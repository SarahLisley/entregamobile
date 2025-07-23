package com.example.myapplication.ui.screens

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BarcodeScannerViewModel(application: Application) : AndroidViewModel(application) {
    private val _barcodeResult = MutableStateFlow("")
    val barcodeResult: StateFlow<String> = _barcodeResult

    fun scanFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            val image = InputImage.fromBitmap(bitmap, 0)
            val scanner = BarcodeScanning.getClient()
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val result = barcodes.joinToString("\n") { it.rawValue ?: "" }
                        _barcodeResult.value = result
                    } else {
                        _barcodeResult.value = "Nenhum código encontrado."
                    }
                }
                .addOnFailureListener {
                    _barcodeResult.value = "Erro ao ler código."
                }
        }
    }
}
