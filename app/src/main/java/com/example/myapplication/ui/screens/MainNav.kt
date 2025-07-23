package com.example.myapplication.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNav() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val usuario = authViewModel.usuarioAtual()
    var startDestination by remember { mutableStateOf("login") }

    LaunchedEffect(usuario) {
        startDestination = if (usuario == null) "login" else "firebase"
        if (usuario == null) {
            navController.navigate("login") {
                popUpTo(0)
            }
        } else {
            navController.navigate("firebase") {
                popUpTo(0)
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("firebase") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("firebase") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Produtos (Realtime DB)") },
                        actions = {
                            TextButton(onClick = { navController.navigate("firestore") }) {
                                Text("Firestore")
                            }
                            TextButton(onClick = { navController.navigate("movement") }) {
                                Text("Movimento")
                            }
                            TextButton(onClick = { navController.navigate("location") }) {
                                Text("Localização")
                            }
                            TextButton(onClick = { navController.navigate("offline") }) {
                                Text("Offline")
                            }
                            TextButton(onClick = { navController.navigate("textrec") }) {
                                Text("Reconhecimento de Texto")
                            }
                            TextButton(onClick = { navController.navigate("barcode") }) {
                                Text("QR Code/Barcode")
                            }
                            TextButton(onClick = { navController.navigate("barcodecam") }) {
                                Text("Barcode Câmera")
                            }
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = {
                                authViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }) {
                                Text("Logout")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                ProdutosFirebaseScreen()
            }
        }
        composable("firestore") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Produtos (Firestore)") },
                        actions = {
                            TextButton(onClick = { navController.navigate("firebase") }) {
                                Text("Realtime DB")
                            }
                            TextButton(onClick = { navController.navigate("movement") }) {
                                Text("Movimento")
                            }
                            TextButton(onClick = { navController.navigate("location") }) {
                                Text("Localização")
                            }
                            TextButton(onClick = { navController.navigate("offline") }) {
                                Text("Offline")
                            }
                            TextButton(onClick = { navController.navigate("textrec") }) {
                                Text("Reconhecimento de Texto")
                            }
                            TextButton(onClick = { navController.navigate("barcode") }) {
                                Text("QR Code/Barcode")
                            }
                            TextButton(onClick = { navController.navigate("barcodecam") }) {
                                Text("Barcode Câmera")
                            }
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = {
                                authViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }) {
                                Text("Logout")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                ProdutosFirestoreScreen()
            }
        }
        composable("movement") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Sensibilidade ao Movimento") },
                        actions = {
                            TextButton(onClick = { navController.navigate("location") }) {
                                Text("Localização")
                            }
                            TextButton(onClick = { navController.navigate("offline") }) {
                                Text("Offline")
                            }
                            TextButton(onClick = { navController.navigate("textrec") }) {
                                Text("Reconhecimento de Texto")
                            }
                            TextButton(onClick = { navController.navigate("barcode") }) {
                                Text("QR Code/Barcode")
                            }
                            TextButton(onClick = { navController.navigate("barcodecam") }) {
                                Text("Barcode Câmera")
                            }
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Voltar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                MovementDemoScreen()
            }
        }
        composable("location") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Sensibilidade à Localização") },
                        actions = {
                            TextButton(onClick = { navController.navigate("offline") }) {
                                Text("Offline")
                            }
                            TextButton(onClick = { navController.navigate("textrec") }) {
                                Text("Reconhecimento de Texto")
                            }
                            TextButton(onClick = { navController.navigate("barcode") }) {
                                Text("QR Code/Barcode")
                            }
                            TextButton(onClick = { navController.navigate("barcodecam") }) {
                                Text("Barcode Câmera")
                            }
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Voltar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                LocationDemoScreen()
            }
        }
        composable("offline") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Produtos Offline (Room)") },
                        actions = {
                            TextButton(onClick = { navController.navigate("textrec") }) {
                                Text("Reconhecimento de Texto")
                            }
                            TextButton(onClick = { navController.navigate("barcode") }) {
                                Text("QR Code/Barcode")
                            }
                            TextButton(onClick = { navController.navigate("barcodecam") }) {
                                Text("Barcode Câmera")
                            }
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Voltar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                ProdutosOfflineScreen()
            }
        }
        composable("textrec") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Reconhecimento de Texto em Imagem") },
                        actions = {
                            TextButton(onClick = { navController.navigate("barcode") }) {
                                Text("QR Code/Barcode")
                            }
                            TextButton(onClick = { navController.navigate("barcodecam") }) {
                                Text("Barcode Câmera")
                            }
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Voltar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                TextRecognitionScreen()
            }
        }
        composable("barcode") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Leitura de QR Code/Barcode") },
                        actions = {
                            TextButton(onClick = { navController.navigate("barcodecam") }) {
                                Text("Barcode Câmera")
                            }
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Voltar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                BarcodeScannerScreen()
            }
        }
        composable("barcodecam") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Leitura de QR Code/Barcode em tempo real") },
                        actions = {
                            TextButton(onClick = { navController.navigate("objectdetection") }) {
                                Text("Reconhecimento de Objetos")
                            }
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Voltar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                BarcodeCameraScreen()
            }
        }
        composable("objectdetection") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Reconhecimento de Objetos em Imagem") },
                        actions = {
                            TextButton(onClick = { navController.navigate("settings") }) {
                                Text("Configurações")
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Voltar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                ObjectDetectionScreen()
            }
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}
