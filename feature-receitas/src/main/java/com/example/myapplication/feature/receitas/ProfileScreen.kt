package com.example.myapplication.feature.receitas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val database = FirebaseDatabase.getInstance()
    
    // Estados para dados pessoais
    var userWeight by remember { mutableStateOf("") }
    var userHeight by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var userGoal by remember { mutableStateOf("") }
    var userActivityLevel by remember { mutableStateOf("") }
    var userAllergies by remember { mutableStateOf("") }
    
    // Estados para estatísticas
    var totalFavorites by remember { mutableStateOf(0) }
    var totalCreated by remember { mutableStateOf(0) }
    var totalLiked by remember { mutableStateOf(0) }
    var weeklyRecipes by remember { mutableStateOf(0) }
    
    // Estados para preferências
    var selectedPreferences by remember { mutableStateOf(setOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // Estados para edição
    var showEditPersonalData by remember { mutableStateOf(false) }
    
    // Preferências expandidas
    val availablePreferences = listOf(
        "vegetariano" to "Vegetariano",
        "vegano" to "Vegano", 
        "sem-gluten" to "Sem Glúten",
        "sem-lactose" to "Sem Lactose",
        "low-carb" to "Low Carb",
        "sem-acucar" to "Sem Açúcar",
        "rapido" to "Receitas Rápidas",
        "saudavel" to "Receitas Saudáveis",
        "tradicional" to "Receitas Tradicionais",
        "doce" to "Doces",
        "salgado" to "Salgados",
        "sem-oleo" to "Sem Óleo",
        "sem-sal" to "Sem Sal",
        "proteico" to "Alto Proteico",
        "fibras" to "Rico em Fibras",
        "vitaminas" to "Rico em Vitaminas"
    )
    
    // Objetivos disponíveis
    val availableGoals = listOf(
        "emagrecer" to "Emagrecer",
        "ganhar-massa" to "Ganhar Massa Muscular", 
        "manter-peso" to "Manter Peso",
        "melhorar-saude" to "Melhorar Saúde",
        "controlar-diabetes" to "Controlar Diabetes",
        "reducao-colesterol" to "Redução de Colesterol"
    )
    
    // Níveis de atividade
    val activityLevels = listOf(
        "sedentario" to "Sedentário",
        "leve" to "Atividade Leve",
        "moderado" to "Atividade Moderada",
        "ativo" to "Muito Ativo",
        "atleta" to "Atleta"
    )
    
    // Carregar dados do usuário
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            try {
                // Carregar preferências
                val preferencesSnapshot = database.getReference("users").child(userId).child("preferences").get().await()
                val preferences = preferencesSnapshot.getValue(String::class.java)?.split(",")?.toSet() ?: emptySet()
                selectedPreferences = preferences
                
                // Carregar dados pessoais
                val personalDataSnapshot = database.getReference("users").child(userId).child("personalData").get().await()
                val personalData = personalDataSnapshot.getValue(Map::class.java) as? Map<String, String>
                personalData?.let {
                    userWeight = it["weight"] ?: ""
                    userHeight = it["height"] ?: ""
                    userAge = it["age"] ?: ""
                    userGoal = it["goal"] ?: ""
                    userActivityLevel = it["activityLevel"] ?: ""
                    userAllergies = it["allergies"] ?: ""
                }
                
                // Carregar estatísticas
                loadUserStatistics(userId, database) { stats ->
                    totalFavorites = stats["favorites"] ?: 0
                    totalCreated = stats["created"] ?: 0
                    totalLiked = stats["liked"] ?: 0
                    weeklyRecipes = stats["weekly"] ?: 0
                }
            } catch (e: Exception) {
                println("Erro ao carregar dados do usuário: ${e.message}")
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            println("DEBUG: Botão de configurações clicado!")
                            onNavigateToSettings()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Seção 1: Informações do usuário com avatar
            item {
                UserInfoCard(
                    currentUser = currentUser,
                    userWeight = userWeight,
                    userHeight = userHeight,
                    userAge = userAge,
                    userGoal = userGoal,
                    userActivityLevel = userActivityLevel,
                    userAllergies = userAllergies,
                    onEditClick = { showEditPersonalData = true },
                    onDataChange = { field, value ->
                        currentUser?.uid?.let { userId ->
                            val updates = mapOf(field to value)
                            database.getReference("users").child(userId).child("personalData")
                                .updateChildren(updates as Map<String, Any>)
                        }
                    }
                )
            }
            
            // Seção 2: Estatísticas do usuário
            item {
                UserStatsCard(
                    totalFavorites = totalFavorites,
                    totalCreated = totalCreated,
                    totalLiked = totalLiked,
                    weeklyRecipes = weeklyRecipes
                )
            }
            
            // Seção 3: Objetivos e Metas
            item {
                GoalsCard(
                    selectedGoal = userGoal,
                    availableGoals = availableGoals,
                    onGoalChange = { goal ->
                        userGoal = goal
                        currentUser?.uid?.let { userId ->
                            database.getReference("users").child(userId).child("personalData")
                                .child("goal").setValue(goal)
                        }
                    }
                )
            }
            
            // Seção 4: Preferências Alimentares Expandidas
            item {
                EnhancedPreferencesCard(
                    selectedPreferences = selectedPreferences,
                    availablePreferences = availablePreferences,
                    onPreferencesChange = { preferences ->
                        selectedPreferences = preferences
                        currentUser?.uid?.let { userId ->
                            val preferencesString = preferences.joinToString(",")
                            database.getReference("users").child(userId).child("preferences")
                                .setValue(preferencesString)
                        }
                    }
                )
            }
            
            // Seção 5: Histórico de Atividades
            item {
                ActivityHistoryCard(
                    onViewFavorites = { /* Navegar para favoritos */ },
                    onViewCreated = { /* Navegar para receitas criadas */ },
                    onViewLiked = { /* Navegar para receitas curtidas */ }
                )
            }
            
            // Seção 6: Configurações
            item {
                SettingsCard(
                    onNavigateToSettings = onNavigateToSettings
                )
            }
            
            // Espaço extra no final
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    
    // Dialog para editar dados pessoais
    if (showEditPersonalData) {
        EditPersonalDataDialog(
            weight = userWeight,
            height = userHeight,
            age = userAge,
            allergies = userAllergies,
            goal = userGoal,
            activityLevel = userActivityLevel,
            availableGoals = availableGoals,
            activityLevels = activityLevels,
            onDismiss = { showEditPersonalData = false },
            onSave = { weight, height, age, allergies, goal, activityLevel ->
                userWeight = weight
                userHeight = height
                userAge = age
                userAllergies = allergies
                userGoal = goal
                userActivityLevel = activityLevel
                
                currentUser?.uid?.let { userId ->
                    val updates = mapOf(
                        "weight" to weight,
                        "height" to height,
                        "age" to age,
                        "allergies" to allergies,
                        "goal" to goal,
                        "activityLevel" to activityLevel
                    )
                    database.getReference("users").child(userId).child("personalData")
                        .updateChildren(updates as Map<String, Any>)
                }
                showEditPersonalData = false
            }
        )
    }
}

@Composable
fun UserInfoCard(
    currentUser: com.google.firebase.auth.FirebaseUser?,
    userWeight: String,
    userHeight: String,
    userAge: String,
    userGoal: String,
    userActivityLevel: String,
    userAllergies: String,
    onEditClick: () -> Unit,
    onDataChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Avatar e informações básicas
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentUser?.displayName ?: "Usuário",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = currentUser?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botão de editar
                    TextButton(
                        onClick = onEditClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar Dados")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resumo dos dados pessoais
            PersonalDataSummary(
                weight = userWeight,
                height = userHeight,
                age = userAge,
                goal = userGoal,
                activityLevel = userActivityLevel,
                allergies = userAllergies
            )
        }
    }
}

@Composable
fun PersonalDataSummary(
    weight: String,
    height: String,
    age: String,
    goal: String,
    activityLevel: String,
    allergies: String
) {
    Column {
        if (weight.isNotBlank() || height.isNotBlank() || age.isNotBlank()) {
            Text(
                text = "Dados Pessoais",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (weight.isNotBlank()) {
                    DataItem(
                        icon = Icons.Default.MonitorWeight,
                        label = "Peso",
                        value = "$weight kg"
                    )
                }
                
                if (height.isNotBlank()) {
                    DataItem(
                        icon = Icons.Default.Straighten,
                        label = "Altura",
                        value = "$height cm"
                    )
                }
                
                if (age.isNotBlank()) {
                    DataItem(
                        icon = Icons.Default.Person,
                        label = "Idade",
                        value = "$age anos"
                    )
                }
            }
        }
        
        if (goal.isNotBlank() || activityLevel.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Objetivos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (goal.isNotBlank()) {
                DataItem(
                    icon = Icons.Default.Flag,
                    label = "Objetivo",
                    value = goal
                )
            }
            
            if (activityLevel.isNotBlank()) {
                DataItem(
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    label = "Nível de Atividade",
                    value = activityLevel
                )
            }
        }
        
        if (allergies.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Restrições",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            DataItem(
                icon = Icons.Default.Warning,
                label = "Alergias",
                value = allergies
            )
        }
    }
}

@Composable
fun DataItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun UserStatsCard(
    totalFavorites: Int,
    totalCreated: Int,
    totalLiked: Int,
    weeklyRecipes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Minhas Estatísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Favorite,
                    value = totalFavorites.toString(),
                    label = "Favoritos"
                )
                StatItem(
                    icon = Icons.Default.Edit,
                    value = totalCreated.toString(),
                    label = "Criadas"
                )
                StatItem(
                    icon = Icons.Default.ThumbUp,
                    value = totalLiked.toString(),
                    label = "Curtidas"
                )
                StatItem(
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    value = weeklyRecipes.toString(),
                    label = "Esta Semana"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun GoalsCard(
    selectedGoal: String,
    availableGoals: List<Pair<String, String>>,
    onGoalChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Meu Objetivo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Selecione seu objetivo principal para receber recomendações personalizadas:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            availableGoals.forEach { (key, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedGoal == key,
                        onClick = { onGoalChange(key) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedPreferencesCard(
    selectedPreferences: Set<String>,
    availablePreferences: List<Pair<String, String>>,
    onPreferencesChange: (Set<String>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Preferências Alimentares",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Selecione suas preferências para receber recomendações personalizadas:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de preferências em grid
            val itemsPerRow = 2
            availablePreferences.chunked(itemsPerRow).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { (key, label) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPreferences.contains(key))
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedPreferences.contains(key),
                                    onCheckedChange = { checked ->
                                        val newPreferences = if (checked) {
                                            selectedPreferences + key
                                        } else {
                                            selectedPreferences - key
                                        }
                                        onPreferencesChange(newPreferences)
                                    }
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedPreferences.contains(key))
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    // Preencher espaço vazio se necessário
                    repeat(itemsPerRow - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ActivityHistoryCard(
    onViewFavorites: () -> Unit,
    onViewCreated: () -> Unit,
    onViewLiked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Histórico de Atividades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botões de navegação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActivityButton(
                    icon = Icons.Default.Favorite,
                    label = "Favoritos",
                    onClick = onViewFavorites
                )
                ActivityButton(
                    icon = Icons.Default.Edit,
                    label = "Criadas",
                    onClick = onViewCreated
                )
                ActivityButton(
                    icon = Icons.Default.ThumbUp,
                    label = "Curtidas",
                    onClick = onViewLiked
                )
            }
        }
    }
}

@Composable
fun ActivityButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPersonalDataDialog(
    weight: String,
    height: String,
    age: String,
    allergies: String,
    goal: String,
    activityLevel: String,
    availableGoals: List<Pair<String, String>>,
    activityLevels: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var editWeight by remember { mutableStateOf(weight) }
    var editHeight by remember { mutableStateOf(height) }
    var editAge by remember { mutableStateOf(age) }
    var editAllergies by remember { mutableStateOf(allergies) }
    var editGoal by remember { mutableStateOf(goal) }
    var editActivityLevel by remember { mutableStateOf(activityLevel) }
    
    // Estados para dropdowns
    var goalExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Editar Dados Pessoais")
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = editWeight,
                    onValueChange = { editWeight = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = editHeight,
                    onValueChange = { editHeight = it },
                    label = { Text("Altura (cm)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = editAge,
                    onValueChange = { editAge = it },
                    label = { Text("Idade") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = editAllergies,
                    onValueChange = { editAllergies = it },
                    label = { Text("Alergias (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Dropdown para objetivo
                ExposedDropdownMenuBox(
                    expanded = goalExpanded,
                    onExpandedChange = { goalExpanded = it },
                ) {
                    OutlinedTextField(
                        value = availableGoals.find { it.first == editGoal }?.second ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Objetivo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = goalExpanded,
                        onDismissRequest = { goalExpanded = false }
                    ) {
                        availableGoals.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { 
                                    editGoal = key
                                    goalExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Dropdown para nível de atividade
                ExposedDropdownMenuBox(
                    expanded = activityExpanded,
                    onExpandedChange = { activityExpanded = it },
                ) {
                    OutlinedTextField(
                        value = activityLevels.find { it.first == editActivityLevel }?.second ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Nível de Atividade") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = activityExpanded,
                        onDismissRequest = { activityExpanded = false }
                    ) {
                        activityLevels.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { 
                                    editActivityLevel = key
                                    activityExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(editWeight, editHeight, editAge, editAllergies, editGoal, editActivityLevel)
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SettingsCard(
    onNavigateToSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Configurações",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Acesse as configurações do app para personalizar sua experiência:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNavigateToSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Abrir Configurações")
            }
        }
    }
}

// Função auxiliar para carregar estatísticas do usuário
private suspend fun loadUserStatistics(userId: String, database: FirebaseDatabase, onStatsLoaded: (Map<String, Int>) -> Unit) {
    try {
        // Buscar receitas favoritadas
        val favoritesSnapshot = database.getReference("receitas")
            .orderByChild("favoritos")
            .get().await()
        
        var totalFavorites = 0
        favoritesSnapshot.children.forEach { snapshot ->
            val favoritos = snapshot.child("favoritos").getValue(List::class.java) as? List<String>
            if (favoritos?.contains(userId) == true) {
                totalFavorites++
            }
        }
        
        // Buscar receitas criadas pelo usuário
        val createdSnapshot = database.getReference("receitas")
            .orderByChild("userId")
            .equalTo(userId)
            .get().await()
        
        val totalCreated = createdSnapshot.childrenCount.toInt()
        
        // Buscar receitas curtidas
        val likedSnapshot = database.getReference("receitas")
            .orderByChild("curtidas")
            .get().await()
        
        var totalLiked = 0
        likedSnapshot.children.forEach { snapshot ->
            val curtidas = snapshot.child("curtidas").getValue(List::class.java) as? List<String>
            if (curtidas?.contains(userId) == true) {
                totalLiked++
            }
        }
        
        // Para simplicidade, vamos usar um valor fixo para receitas da semana
        val weeklyRecipes = (totalFavorites + totalCreated + totalLiked) / 3
        
        onStatsLoaded(
            mapOf(
                "favorites" to totalFavorites,
                "created" to totalCreated,
                "liked" to totalLiked,
                "weekly" to weeklyRecipes
            )
        )
    } catch (e: Exception) {
        println("Erro ao carregar estatísticas: ${e.message}")
        onStatsLoaded(emptyMap())
    }
} 