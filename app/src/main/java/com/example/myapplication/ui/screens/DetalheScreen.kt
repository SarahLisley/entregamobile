package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.FirebaseRepository
import com.example.myapplication.data.SupabaseImageUploader
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.screens.ReceitasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(navController: NavHostController, receitaId: String?) {
    val firebaseRepository = remember { FirebaseRepository() }
    var receita by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(receitaId) {
        if (receitaId != null) {
            firebaseRepository.escutarReceitas { data ->
                val found = data?.values?.mapNotNull { it as? Map<String, Any> }
                    ?.find { it["id"]?.toString() == receitaId.toString() }
                receita = found
                isFavorite = found?.get("isFavorita") as? Boolean ?: false
            }
        }
    }
    val receitaLocal = receita
    if (showEditDialog && receitaLocal != null) {
        var nome by remember { mutableStateOf(receitaLocal["nome"] as? String ?: "") }
        var descricao by remember { mutableStateOf(receitaLocal["descricaoCurta"] as? String ?: "") }
        var tempo by remember { mutableStateOf(receitaLocal["tempoPreparo"] as? String ?: "") }
        var porcoes by remember { mutableStateOf((receitaLocal["porcoes"] as? Number)?.toString() ?: "") }
        var ingredientes by remember { mutableStateOf((receitaLocal["ingredientes"] as? List<*>)?.joinToString("\n") ?: "") }
        var modoPreparo by remember { mutableStateOf((receitaLocal["modoPreparo"] as? List<*>)?.joinToString("\n") ?: "") }
        var imagemUri by remember { mutableStateOf<Uri?>(null) }
        var isUploading by remember { mutableStateOf(false) }
        var uploadError by remember { mutableStateOf<String?>(null) }
        val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) imagemUri = uri
        }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Receita") },
            text = {
                Column {
                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text(if (imagemUri == null) "Selecionar nova imagem" else "Imagem Selecionada")
                    }
                    Spacer(Modifier.height(8.dp))
                    if (isUploading) {
                        Text("Enviando imagem...", color = MaterialTheme.colorScheme.primary)
                    }
                    if (uploadError != null) {
                        Text("Erro ao enviar imagem: $uploadError", color = MaterialTheme.colorScheme.error)
                    }
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome da receita") }
                    )
                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição curta") }
                    )
                    OutlinedTextField(
                        value = tempo,
                        onValueChange = { tempo = it },
                        label = { Text("Tempo de preparo") }
                    )
                    OutlinedTextField(
                        value = porcoes,
                        onValueChange = { porcoes = it.filter { c -> c.isDigit() } },
                        label = { Text("Porções") }
                    )
                    OutlinedTextField(
                        value = ingredientes,
                        onValueChange = { ingredientes = it },
                        label = { Text("Ingredientes (um por linha)") },
                        maxLines = 4
                    )
                    OutlinedTextField(
                        value = modoPreparo,
                        onValueChange = { modoPreparo = it },
                        label = { Text("Modo de preparo (um por linha)") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val id = receitaId
                    val oldImageUrl = receitaLocal["imagemUrl"] as? String
                    if (id != null) {
                        scope.launch {
                            var imageUrl: String? = oldImageUrl
                            if (imagemUri != null) {
                                // Deletar imagem antiga do Supabase, se houver
                                if (!oldImageUrl.isNullOrBlank()) {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val supabaseUrl = "https://zfbkkrtpnoteapbxfuos.supabase.co"
                                            val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpmYmtrcnRwbm90ZWFwYnhmdW9zIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTMzNzgxMzIsImV4cCI6MjA2ODk1NDEzMn0.-hvEHVZY08vBKkFlK3fqIBhOs1_8HzIzGCop2OurB_U"
                                            val path = oldImageUrl.substringAfter("/object/public/")
                                            val request = okhttp3.Request.Builder()
                                                .url("$supabaseUrl/storage/v1/object/receitas/$path")
                                                .addHeader("apikey", supabaseKey)
                                                .addHeader("Authorization", "Bearer $supabaseKey")
                                                .delete()
                                                .build()
                                            val client = okhttp3.OkHttpClient()
                                            client.newCall(request).execute()
                                        } catch (_: Exception) {}
                                    }
                                }
                                isUploading = true
                                uploadError = null
                                try {
                                    imageUrl = SupabaseImageUploader.uploadImage(context = context, imageUri = imagemUri!!)
                                } catch (e: Exception) {
                                    uploadError = e.message
                                }
                                isUploading = false
                            }
                            firebaseRepository.db.child(id).updateChildren(
                                mapOf(
                                    "nome" to nome,
                                    "descricaoCurta" to descricao,
                                    "tempoPreparo" to tempo,
                                    "porcoes" to porcoes.toIntOrNull(),
                                    "ingredientes" to ingredientes.split('\n').filter { it.isNotBlank() },
                                    "modoPreparo" to modoPreparo.split('\n').filter { it.isNotBlank() },
                                    "imagemUrl" to (imageUrl ?: "")
                                )
                            )
                            showEditDialog = false
                        }
                    }
                }, enabled = !isUploading) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = {
                        // Deletar receita e imagem
                        val id = receitaId
                        val imgUrl = receita?.get("imagemUrl") as? String
                        scope.launch {
                            if (id != null) {
                                firebaseRepository.db.child(id).removeValue()
                                if (!imgUrl.isNullOrBlank()) {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val supabaseUrl = "https://zfbkkrtpnoteapbxfuos.supabase.co"
                                            val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpmYmtrcnRwbm90ZWFwYnhmdW9zIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTMzNzgxMzIsImV4cCI6MjA2ODk1NDEzMn0.-hvEHVZY08vBKkFlK3fqIBhOs1_8HzIzGCop2OurB_U"
                                            val path = imgUrl.substringAfter("/object/public/")
                                            val request = okhttp3.Request.Builder()
                                                .url("$supabaseUrl/storage/v1/object/receitas/$path")
                                                .addHeader("apikey", supabaseKey)
                                                .addHeader("Authorization", "Bearer $supabaseKey")
                                                .delete()
                                                .build()
                                            val client = okhttp3.OkHttpClient()
                                            client.newCall(request).execute()
                                        } catch (_: Exception) {}
                                    }
                                }
                                navController.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Deletar")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        receita?.let { r ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Exibir imagem se disponível
                val imagemUrl = r["imagemUrl"] as? String ?: ""
                if (imagemUrl.isNotBlank()) {
                    AsyncImage(
                        model = imagemUrl,
                        contentDescription = r["nome"] as? String ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = r["nome"] as? String ?: "", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = r["descricaoCurta"] as? String ?: "", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ingredientes:", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (r["ingredientes"] as? List<*>)?.forEach { ingrediente ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = ingrediente.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Modo de Preparo:", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (r["modoPreparo"] as? List<*>)?.forEachIndexed { index, passo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = passo.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        val newFav = !isFavorite
                        isFavorite = newFav
                        // Atualiza o campo isFavorita no Firebase
                        if (receitaId != null) {
                            scope.launch {
                                firebaseRepository.db.child(receitaId).child("isFavorita").setValue(newFav)
                            }
                        }
                    }) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFavorite) "Remover dos Favoritos" else "Adicionar aos Favoritos")
                    }
                    // Remover o botão 'ouvir/ver'
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Receitas Relacionadas:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Exemplo de receitas relacionadas (placeholder)
                    items(3) { idx ->
                        Card(
                            modifier = Modifier
                                .width(160.dp)
                                .height(170.dp),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Imagem placeholder
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Receita ${idx + 1}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Receita não encontrada.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
