@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
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
import com.example.myapplication.core.data.network.GeminiServiceImpl
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.ui.theme.OrangeSecondary

@Composable
fun ConfiguracoesScreen(
    onBack: () -> Unit,
    navController: NavController? = null
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
    val receitasRepository = remember {
        ReceitasRepository(
            receitaDao,
            database.nutritionDataDao(),
            connectivityObserver,
            com.example.myapplication.core.data.storage.ImageStorageService(),
            com.example.myapplication.core.ui.error.ErrorHandler()
        )
    }
    val nutritionRepository = remember { NutritionRepository(context, GeminiServiceImpl("AIzaSyDiwB3lig9_fvI5wbBlILl32Ztqj41XO2I")) }
    val dataSeeder = remember { DataSeeder(context, receitasRepository, nutritionRepository) }
    var receitasFirebase by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var showReceitaDialog by remember { mutableStateOf(false) }
    var receitaSelecionada by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var isSeedingDatabase by remember { mutableStateOf(false) }
    val darkMode by viewModel.isDarkModeEnabled.collectAsState()

    // Debug: Log para verificar se a tela está sendo carregada
    LaunchedEffect(Unit) {
        println("DEBUG: ConfiguracoesScreen carregada")
        println("DEBUG: Dark mode inicial: $darkMode")
    }

    LaunchedEffect(darkMode) {
        println("DEBUG: Dark mode mudou para: $darkMode")
    }

    LaunchedEffect(Unit) {
        // Iniciar escuta do Firebase (atualiza o Room automaticamente)
        receitasRepository.escutarReceitas { }
        
        // Buscar receitas do Room
        receitasRepository.getReceitas().collect { receitas ->
            receitasFirebase = receitas.map { it.toMap() }
            println("DEBUG: Receitas carregadas: ${receitas.size}")
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Seção de Aparência
            item {
                SettingsSection(title = "Aparência", icon = Icons.Filled.Palette) {
                    ThemePreferenceRow(
                        isDarkMode = darkMode,
                        onThemeChange = viewModel::setDarkMode
                    )
                }
            }
            
            // Seção de Notificações
            item {
                SettingsSection(title = "Notificações", icon = Icons.Filled.Notifications) {
                    Button(
                        onClick = { 
                            if (NotificationHelper.hasExactAlarmPermission(context)) {
                                showReceitaDialog = true
                            } else {
                                showPermissionDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Agendar lembrete")
                        Spacer(Modifier.width(8.dp))
                        Text("Escolher Receita e Horário")
                    }
                }
            }
            
            // Seção de Dados
            item {
                SettingsSection(title = "Dados", icon = Icons.Filled.Storage) {
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
                        modifier = Modifier.fillMaxWidth()
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
                }
            }
        }
        
        // Diálogos
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

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = GreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ThemePreferenceRow(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                contentDescription = if (isDarkMode) "Modo Escuro" else "Modo Claro",
                tint = if (isDarkMode) OrangeSecondary else GreenPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isDarkMode) "Modo Escuro" else "Modo Claro",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = if (isDarkMode) "Tema escuro ativado" else "Tema claro ativado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        Switch(
            checked = isDarkMode,
            onCheckedChange = onThemeChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OrangeSecondary,
                checkedTrackColor = OrangeSecondary.copy(alpha = 0.5f),
                uncheckedThumbColor = GreenPrimary,
                uncheckedTrackColor = GreenPrimary.copy(alpha = 0.5f)
            )
        )
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
