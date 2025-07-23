@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.myapplication.data.UserPreferencesRepository
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Notifications
import java.util.Calendar
import android.app.TimePickerDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.myapplication.model.Receita
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.setValue
import com.example.myapplication.ui.screens.ReceitasViewModel
import com.example.myapplication.notifications.NotificationHelper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun ConfiguracoesScreen(
    onBack: () -> Unit,
    receitasViewModel: ReceitasViewModel = viewModel()
) {
    // Cria o ViewModel com sua factory
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            UserPreferencesRepository(context)
        )
    )

    // Coleta o estado do DataStore
    val darkMode by viewModel.isDarkModeEnabled.collectAsState()
    var showReceitaDialog by remember { mutableStateOf(false) }
    var receitaSelecionada by remember { mutableStateOf<Receita?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
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
            Button(onClick = { showReceitaDialog = true }, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Filled.Notifications, contentDescription = "Agendar lembrete")
                Spacer(Modifier.width(8.dp))
                Text("Escolher Receita e Horário")
            }
            if (showReceitaDialog) {
                AlertDialog(
                    onDismissRequest = { showReceitaDialog = false },
                    title = { Text("Selecione uma receita") },
                    text = {
                        LazyColumn(modifier = Modifier.height(200.dp)) {
                            items(receitasViewModel.receitas) { receita ->
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
                                    Text(receita.nome)
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
                        NotificationHelper.scheduleReminder(
                            context = context,
                            itemId = receitaSelecionada!!.id,
                            title = receitaSelecionada!!.nome,
                            timeInMillis = calendar.timeInMillis
                        )
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
