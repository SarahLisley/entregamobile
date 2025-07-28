package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.ui.theme.OrangeSecondary
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication.feature.receitas.ReceitasViewModel
import com.example.myapplication.ui.screens.ViewModelFactory

import com.example.myapplication.feature.receitas.ReceitasUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(
    navController: NavHostController,
    receitaId: String?,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Obtenha o ViewModel com escopo independente para esta tela
    val receitasViewModel: ReceitasViewModel = viewModel(
        factory = ViewModelFactory(context)
    )

    // Observe o estado da receita selecionada
    val selectedRecipeUiState by receitasViewModel.selectedRecipeState.collectAsState()
    val nutritionState by receitasViewModel.nutritionState.collectAsState()

    // Carregue a receita específica quando a tela for criada
    LaunchedEffect(receitaId) {
        receitasViewModel.loadRecipeById(receitaId)
    }

    // Estados de edição
    val startInEditMode = backStackEntry.arguments?.getBoolean("startInEditMode") ?: false
    var showEditDialog by remember(startInEditMode) { mutableStateOf(startInEditMode) }

    var editNome by remember { mutableStateOf("") }
    var editDescricao by remember { mutableStateOf("") }
    var editTempo by remember { mutableStateOf("") }
    var editPorcoes by remember { mutableStateOf("") }
    var editIngredientes by remember { mutableStateOf("") }
    var editModoPreparo by remember { mutableStateOf("") }
    var editImagemUri by remember { mutableStateOf<Uri?>(null) }
    
    // Estados de validação
    var nomeError by remember { mutableStateOf("") }
    var descricaoError by remember { mutableStateOf("") }
    var tempoError by remember { mutableStateOf("") }
    var porcoesError by remember { mutableStateOf("") }
    var ingredientesError by remember { mutableStateOf("") }
    var modoPreparoError by remember { mutableStateOf("") }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) editImagemUri = uri
    }

    val receita = (selectedRecipeUiState as? ReceitasUiState.Success)?.receitas?.firstOrNull()
    val isLoading = selectedRecipeUiState is ReceitasUiState.Loading
    val isError = selectedRecipeUiState is ReceitasUiState.Error
    
    // Debug: Log para verificar o receitaId e a receita carregada
    LaunchedEffect(receitaId, selectedRecipeUiState) {
        println("DEBUG: DetalheScreen - receitaId recebido: '$receitaId'")
        if (selectedRecipeUiState is ReceitasUiState.Success) {
            val receitas = (selectedRecipeUiState as ReceitasUiState.Success).receitas
            println("DEBUG: DetalheScreen - Receita carregada: ${receitas.firstOrNull()?.nome}")
            println("DEBUG: DetalheScreen - Receita encontrada: ${receita != null}")
        }
    }
    
    // Verificar se o usuário atual é o autor da receita
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isOwner = remember(receita, currentUser) {
        receita?.userId == currentUser?.uid
    }
    
    // Debug: Log para verificar os IDs
    LaunchedEffect(receita, currentUser) {
        if (receita != null && currentUser != null) {
            println("DEBUG: Receita userId: ${receita.userId}")
            println("DEBUG: Current user uid: ${currentUser.uid}")
            println("DEBUG: isOwner: $isOwner")
            println("DEBUG: Receita nome: ${receita.nome}")
            println("DEBUG: Current user email: ${currentUser.email}")
        }
    }

    LaunchedEffect(receita, showEditDialog) {
        if (receita != null && showEditDialog) {
            editNome = receita.nome
            editDescricao = receita.descricaoCurta
            editTempo = receita.tempoPreparo
            editPorcoes = receita.porcoes.toString()
            editIngredientes = receita.ingredientes.joinToString("\n")
            editModoPreparo = receita.modoPreparo.joinToString("\n")
            editImagemUri = null
        }
    }

    LaunchedEffect(Unit) {
        receitasViewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            receitasViewModel.limparInformacoesNutricionais()
        }
    }
    
    // Funções de validação
    fun validateForm(): Boolean {
        var isValid = true
        
        if (editNome.isBlank()) {
            nomeError = "Nome é obrigatório"
            isValid = false
        } else if (editNome.length < 3) {
            nomeError = "Nome deve ter pelo menos 3 caracteres"
            isValid = false
        } else {
            nomeError = ""
        }
        
        if (editDescricao.isBlank()) {
            descricaoError = "Descrição é obrigatória"
            isValid = false
        } else if (editDescricao.length < 10) {
            descricaoError = "Descrição deve ter pelo menos 10 caracteres"
            isValid = false
        } else {
            descricaoError = ""
        }
        
        if (editTempo.isBlank()) {
            tempoError = "Tempo de preparo é obrigatório"
            isValid = false
        } else {
            tempoError = ""
        }
        
        val porcoes = editPorcoes.toIntOrNull()
        if (editPorcoes.isBlank()) {
            porcoesError = "Número de porções é obrigatório"
            isValid = false
        } else if (porcoes == null || porcoes <= 0) {
            porcoesError = "Porções deve ser um número maior que 0"
            isValid = false
        } else {
            porcoesError = ""
        }
        
        val ingredientesList = editIngredientes.split('\n').filter { it.isNotBlank() }
        if (ingredientesList.isEmpty()) {
            ingredientesError = "Pelo menos um ingrediente é obrigatório"
            isValid = false
        } else {
            ingredientesError = ""
        }
        
        val modoPreparoList = editModoPreparo.split('\n').filter { it.isNotBlank() }
        if (modoPreparoList.isEmpty()) {
            modoPreparoError = "Pelo menos um passo é obrigatório"
            isValid = false
        } else {
            modoPreparoError = ""
        }
        
        return isValid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (receita != null) {
                        // Botão de análise nutricional
                        IconButton(onClick = { 
                            receitasViewModel.buscarInformacoesNutricionais(receita.nome)
                        }) {
                            Icon(
                                Icons.Filled.Analytics, 
                                contentDescription = "Análise Nutricional",
                                tint = Color.White
                            )
                        }
                        
                        // Botões de edição e exclusão apenas para o autor
                        if (isOwner) {
                            IconButton(onClick = { showEditDialog = true }) {
                                Icon(
                                    Icons.Filled.Edit, 
                                    contentDescription = "Editar",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = {
                                receitasViewModel.deletarReceita(receita.id, receita.imagemUrl)
                                navController.popBackStack()
                            }) {
                                Icon(
                                    Icons.Filled.Delete, 
                                    contentDescription = "Deletar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) { 
                    CircularProgressIndicator(color = GreenPrimary) 
                }
            }
            isError -> {
                val msg = (selectedRecipeUiState as ReceitasUiState.Error).error.message
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues), 
                    contentAlignment = Alignment.Center
                ) {
                    Text("Erro ao carregar: $msg")
                }
            }
            receita != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Hero Section com imagem e informações principais
                    item {
                        HeroSection(receita = receita)
                    }
                    
                    // Seção de estatísticas rápidas
                    item {
                        StatisticsSection(receita = receita)
                    }
                    
                    // Seção de descrição
                    item {
                        DescriptionSection(receita = receita)
                    }
                    
                    // Seção de ingredientes
                    item {
                        IngredientsSection(receita = receita)
                    }
                    
                    // Seção de modo de preparo
                    item {
                        PreparationSection(receita = receita)
                    }
                    
                    // Seção de análise nutricional
                    if (nutritionState != null) {
                        item {
                            NutritionSection(nutritionState = nutritionState!!)
                        }
                    }
                    
                    // Seção de ações (curtir, favoritar)
                    item {
                        ActionsSection(
                            receita = receita,
                            receitasViewModel = receitasViewModel,
                            currentUser = currentUser,
                            isOwner = isOwner
                        )
                    }
                    
                    // Espaçamento final
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { 
                    Text(
                        "Receita não encontrada.", 
                        style = MaterialTheme.typography.bodyLarge
                    ) 
                }
            }
        }

        // Dialog de edição
        if (showEditDialog && receita != null) {
            EditRecipeDialog(
                receita = receita,
                editNome = editNome,
                onEditNomeChange = { 
                    editNome = it
                    if (nomeError.isNotEmpty()) nomeError = ""
                },
                editDescricao = editDescricao,
                onEditDescricaoChange = { 
                    editDescricao = it
                    if (descricaoError.isNotEmpty()) descricaoError = ""
                },
                editTempo = editTempo,
                onEditTempoChange = { 
                    editTempo = it
                    if (tempoError.isNotEmpty()) tempoError = ""
                },
                editPorcoes = editPorcoes,
                onEditPorcoesChange = { 
                    editPorcoes = it.filter { c -> c.isDigit() }
                    if (porcoesError.isNotEmpty()) porcoesError = ""
                },
                editIngredientes = editIngredientes,
                onEditIngredientesChange = { 
                    editIngredientes = it
                    if (ingredientesError.isNotEmpty()) ingredientesError = ""
                },
                editModoPreparo = editModoPreparo,
                onEditModoPreparoChange = { 
                    editModoPreparo = it
                    if (modoPreparoError.isNotEmpty()) modoPreparoError = ""
                },
                editImagemUri = editImagemUri,
                onImagePickerClick = { imagePickerLauncher.launch("image/*") },
                nomeError = nomeError,
                descricaoError = descricaoError,
                tempoError = tempoError,
                porcoesError = porcoesError,
                ingredientesError = ingredientesError,
                modoPreparoError = modoPreparoError,
                onDismiss = { showEditDialog = false },
                onSave = {
                    if (validateForm()) {
                        receitasViewModel.editarReceita(
                            context = context,
                            id = receita.id,
                            nome = editNome,
                            descricaoCurta = editDescricao,
                            novaImagemUri = editImagemUri,
                            ingredientes = editIngredientes.split('\n').filter { it.isNotBlank() },
                            modoPreparo = editModoPreparo.split('\n').filter { it.isNotBlank() },
                            tempoPreparo = editTempo,
                            porcoes = editPorcoes.toIntOrNull() ?: 1,
                            imagemUrlAntiga = receita.imagemUrl
                        )
                        showEditDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun HeroSection(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Imagem de fundo
        AsyncImage(
            model = receita.imagemUrl,
            contentDescription = receita.nome,
            modifier = Modifier.fillMaxSize()
        )
        
        // Gradiente sobreposto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        
        // Informações da receita
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = receita.nome,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "por ${receita.userEmail ?: "Usuário"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun StatisticsSection(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Filled.Timer,
                value = receita.tempoPreparo,
                label = "Tempo"
            )
            StatItem(
                icon = Icons.Filled.Restaurant,
                value = receita.ingredientes.size.toString(),
                label = "Ingredientes"
            )
            StatItem(
                icon = Icons.Filled.People,
                value = receita.porcoes.toString(),
                label = "Porções"
            )
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = GreenPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun DescriptionSection(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Descrição",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = receita.descricaoCurta,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun IngredientsSection(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity) {
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
            Text(
                text = "Ingredientes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            receita.ingredientes.forEach { ingrediente ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreenPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = ingrediente,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun PreparationSection(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity) {
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
            Text(
                text = "Modo de Preparo",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            receita.modoPreparo.forEachIndexed { index, passo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = GreenPrimary,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = passo,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionSection(nutritionState: com.example.myapplication.core.data.model.RecipeNutrition) {
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
                    imageVector = Icons.Filled.Analytics,
                    contentDescription = "Análise Nutricional",
                    tint = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Análise Nutricional",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grid de informações nutricionais
            Column {
                NutritionRow("Calorias", "${nutritionState.calories.toInt()} kcal", true)
                NutritionRow("Proteínas", "${nutritionState.protein.toInt()}g", false)
                NutritionRow("Gorduras", "${nutritionState.fat.toInt()}g", false)
                NutritionRow("Carboidratos", "${nutritionState.carbohydrates.toInt()}g", false)
                nutritionState.fiber?.let { fiber ->
                    NutritionRow("Fibras", "${fiber.toInt()}g", false)
                }
                nutritionState.sugar?.let { sugar ->
                    NutritionRow("Açúcares", "${sugar.toInt()}g", false)
                }
            }
        }
    }
}

@Composable
fun NutritionRow(
    label: String,
    value: String,
    isHighlighted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlighted) GreenPrimary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isHighlighted) GreenPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ActionsSection(
    receita: com.example.myapplication.core.data.database.entity.ReceitaEntity,
    receitasViewModel: ReceitasViewModel,
    currentUser: com.google.firebase.auth.FirebaseUser?,
    isOwner: Boolean
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
            // Indicador de propriedade
            if (isOwner) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sua receita",
                        style = MaterialTheme.typography.bodySmall,
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botão Curtir
                val isLiked = currentUser?.uid in receita.curtidas
                IconButton(
                    onClick = {
                        currentUser?.uid?.let { userId ->
                            receitasViewModel.curtirReceita(receita.id, userId, receita.curtidas)
                        }
                    }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.ThumbUp,
                            contentDescription = "Curtir",
                            tint = if (isLiked) OrangeSecondary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = receita.curtidas.size.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                // Botão Favoritar
                val isFavorited = currentUser?.uid in receita.favoritos
                IconButton(
                    onClick = {
                        currentUser?.uid?.let { userId ->
                            receitasViewModel.favoritarReceita(receita.id, userId, receita.favoritos)
                        }
                    }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorited) OrangeSecondary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = receita.favoritos.size.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditRecipeDialog(
    receita: com.example.myapplication.core.data.database.entity.ReceitaEntity,
    editNome: String,
    onEditNomeChange: (String) -> Unit,
    editDescricao: String,
    onEditDescricaoChange: (String) -> Unit,
    editTempo: String,
    onEditTempoChange: (String) -> Unit,
    editPorcoes: String,
    onEditPorcoesChange: (String) -> Unit,
    editIngredientes: String,
    onEditIngredientesChange: (String) -> Unit,
    editModoPreparo: String,
    onEditModoPreparoChange: (String) -> Unit,
    editImagemUri: Uri?,
    onImagePickerClick: () -> Unit,
    nomeError: String,
    descricaoError: String,
    tempoError: String,
    porcoesError: String,
    ingredientesError: String,
    modoPreparoError: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Receita") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Button(
                    onClick = onImagePickerClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (editImagemUri == null) "Selecionar nova imagem" else "Nova Imagem Selecionada!")
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editNome, 
                    onValueChange = onEditNomeChange, 
                    label = { Text("Nome da receita") },
                    isError = nomeError.isNotEmpty(),
                    supportingText = { if (nomeError.isNotEmpty()) Text(nomeError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editDescricao, 
                    onValueChange = onEditDescricaoChange, 
                    label = { Text("Descrição curta") },
                    isError = descricaoError.isNotEmpty(),
                    supportingText = { if (descricaoError.isNotEmpty()) Text(descricaoError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editTempo, 
                    onValueChange = onEditTempoChange, 
                    label = { Text("Tempo de preparo") },
                    isError = tempoError.isNotEmpty(),
                    supportingText = { if (tempoError.isNotEmpty()) Text(tempoError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editPorcoes, 
                    onValueChange = onEditPorcoesChange, 
                    label = { Text("Porções") },
                    isError = porcoesError.isNotEmpty(),
                    supportingText = { if (porcoesError.isNotEmpty()) Text(porcoesError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editIngredientes, 
                    onValueChange = onEditIngredientesChange, 
                    label = { Text("Ingredientes (um por linha)") }, 
                    maxLines = 4,
                    isError = ingredientesError.isNotEmpty(),
                    supportingText = { if (ingredientesError.isNotEmpty()) Text(ingredientesError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editModoPreparo, 
                    onValueChange = onEditModoPreparoChange, 
                    label = { Text("Modo de preparo (um por linha)") }, 
                    maxLines = 4,
                    isError = modoPreparoError.isNotEmpty(),
                    supportingText = { if (modoPreparoError.isNotEmpty()) Text(modoPreparoError) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = editNome.isNotBlank() && editDescricao.isNotBlank() && 
                         editTempo.isNotBlank() && editPorcoes.isNotBlank() &&
                         editIngredientes.isNotBlank() && editModoPreparo.isNotBlank()
            ) { 
                Text("Salvar") 
            }
        },
        dismissButton = { 
            Button(onClick = onDismiss) { 
                Text("Cancelar") 
            } 
        }
    )
}
