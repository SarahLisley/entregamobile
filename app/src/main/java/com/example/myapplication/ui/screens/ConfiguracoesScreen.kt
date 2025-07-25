@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.core.data.repository.UserPreferencesRepository
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import java.util.Calendar
import android.app.TimePickerDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.size
import com.example.myapplication.notifications.NotificationHelper
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.repository.DataSeeder
import com.example.myapplication.core.data.repository.NutritionRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import android.widget.Toast
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.TextButton

@Composable
fun ConfiguracoesScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            UserPreferencesRepository(context)
        )
    )
    val database = remember { AppDatabase.getDatabase(context) }
    val receitaDao = remember { database.receitaDao() }
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val receitasRepository = remember { ReceitasRepository(receitaDao, connectivityObserver, com.example.myapplication.core.data.storage.ImageStorageService(), com.example.myapplication.core.ui.error.ErrorHandler()) }
    val nutritionRepository = remember { NutritionRepository(context) }
    val dataSeeder = remember { DataSeeder(context, receitasRepository, nutritionRepository) }
    var receitasFirebase by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var showReceitaDialog by remember { mutableStateOf(false) }
    var receitaSelecionada by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var isSeedingDatabase by remember { mutableStateOf(false) }
    val darkMode by viewModel.isDarkModeEnabled.collectAsState()

    LaunchedEffect(Unit) {
        // Iniciar escuta do Firebase (atualiza o Room automaticamente)
        receitasRepository.escutarReceitas { }
        
        // Buscar receitas do Room
        receitasRepository.getReceitas().collect { receitas ->
            receitasFirebase = receitas.map { it.toMap() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            PreferenceRow(
                title = "Modo Escuro",
                isChecked = darkMode,
                onCheckedChange = viewModel::setDarkMode
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { 
                if (NotificationHelper.hasExactAlarmPermission(context)) {
                    showReceitaDialog = true
                } else {
                    showPermissionDialog = true
                }
            }, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Filled.Notifications, contentDescription = "Agendar lembrete")
                Spacer(Modifier.width(8.dp))
                Text("Escolher Receita e Horário")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botão para popular banco de dados (apenas para admin)
            Button(
                onClick = {
                    scope.launch {
                        isSeedingDatabase = true
                        try {
                            val success = dataSeeder.forcePopulateDatabase()
                            if (success) {
                                Toast.makeText(context, "Banco de dados populado com sucesso!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Erro ao popular banco de dados", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isSeedingDatabase = false
                        }
                    }
                },
                enabled = !isSeedingDatabase,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                if (isSeedingDatabase) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.Storage, contentDescription = "Popular banco")
                }
                Spacer(Modifier.width(8.dp))
                Text(if (isSeedingDatabase) "Populando..." else "Popular Banco de Dados")
            }
            
            // Diálogo de permissão
            if (showPermissionDialog) {
                AlertDialog(
                    onDismissRequest = { showPermissionDialog = false },
                    title = { Text("Permissão Necessária") },
                    text = { 
                        Text("Para agendar lembretes, o app precisa de permissão para agendar alarmas exatos. Por favor, vá para as configurações do sistema e conceda essa permissão.")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            context.startActivity(intent)
                            showPermissionDialog = false
                        }) {
                            Text("Ir para Configurações")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
            
            if (showReceitaDialog) {
                AlertDialog(
                    onDismissRequest = { showReceitaDialog = false },
                    title = { Text("Selecione uma receita") },
                    text = {
                        LazyColumn(modifier = Modifier.height(200.dp)) {
                            items(receitasFirebase) { receita ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            receitaSelecionada = receita
                                            showReceitaDialog = false
                                            showTimePicker = true
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(com.example.myapplication.R.drawable.placeholder_shimmer),
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(receita["nome"] as? String ?: "")
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        Button(onClick = { showReceitaDialog = false }) { Text("Cancelar") }
                    }
                )
            }
            if (showTimePicker && receitaSelecionada != null) {
                val now = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hour: Int, minute: Int ->
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val success = NotificationHelper.scheduleReminder(
                            context = context,
                            itemId = receitaSelecionada!!["id"].toString(),
                            title = receitaSelecionada!!["nome"] as? String ?: "",
                            timeInMillis = calendar.timeInMillis
                        )
                        
                        if (success) {
                            Toast.makeText(context, "Lembrete agendado com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Erro ao agendar lembrete. Verifique as permissões do app.", Toast.LENGTH_LONG).show()
                        }
                        showTimePicker = false
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
                ).show()
                showTimePicker = false
            }
        }
    }
}

@Composable
fun PreferenceRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}
