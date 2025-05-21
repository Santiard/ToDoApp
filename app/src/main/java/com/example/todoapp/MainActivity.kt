package com.example.todoapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.todoapp.ui.theme.ToDoAppTheme
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppTheme {
                val navController = rememberNavController()
                val taskViewModel: TaskViewModel = viewModel()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "taskList") {
                        composable("taskList") {
                            TaskListScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        composable("addTask") {

                            AddTaskScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoAppTheme {

    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 700) // Ajusta el tamaño de la preview
@RequiresApi(Build.VERSION_CODES.O) // Necesario ya que las pantallas internas usan java.time
@Composable
fun MainAppPreview() {
    ToDoAppTheme { // Usa el nombre correcto de tu tema
        val navController = rememberNavController()
        val taskViewModel: TaskViewModel = viewModel()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Este NavHost simula el que tienes en tu MainActivity
            NavHost(navController = navController, startDestination = "taskList") {
                composable("taskList") {
                    TaskListScreen(navController = navController, taskViewModel = taskViewModel)
                }
                composable("addTask") {
                    AddTaskScreen(navController = navController, taskViewModel = taskViewModel)
                }
                // Si tuvieras más rutas, las añadirías aquí para que la preview las conozca.
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCard(task: Task, taskViewModel: TaskViewModel, isSelected: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                taskViewModel.toggleTaskSelection(task.name)
            },

        colors = CardDefaults.cardColors(
            containerColor = if (task.status) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.status) TextDecoration.LineThrough else null,
                    color = if (task.status) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (task.status) TextDecoration.LineThrough else null,
                    color = if (task.status) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = "Tipo: ${task.type}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (task.status) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "Límite: ${task.deadline.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (task.status) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { _ ->
                    taskViewModel.toggleTaskSelection(task.name)
                },
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskListScreen(navController: NavController, taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.displayedTasks.collectAsState()
    val searchText by taskViewModel.searchText.collectAsState()
    val filterType by taskViewModel.filterType.collectAsState()
    val selectedTaskIds by taskViewModel.selectedTaskIds.collectAsState()


    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mis Tareas") },
                    actions = {
                        IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filtrar Tareas")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            TaskViewModel.TaskFilter.values().forEach { filterOption ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = when (filterOption) {
                                                TaskViewModel.TaskFilter.ALL -> "Todas"
                                                TaskViewModel.TaskFilter.PENDING -> "Pendientes"
                                                TaskViewModel.TaskFilter.COMPLETED -> "Completadas"
                                            },
                                            color = if (filterType == filterOption.name) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                        )
                                    },
                                    onClick = {
                                        taskViewModel.updateFilterType(filterOption)
                                        showFilterMenu = false
                                    }
                                )
                            }
                        }
                    }
                )

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { taskViewModel.updateSearchText(it) },
                    label = { Text("Buscar tarea por nombre, descripción, tipo o fecha...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Icono de búsqueda") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    singleLine = true
                )
            }
        },

        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {

                val enableCheckButton = selectedTaskIds.isNotEmpty()
                ExtendedFloatingActionButton(
                    text = { Text("Marcar como Completadas") },
                    icon = { Icon(Icons.Filled.DoneAll, contentDescription = "Marcar seleccionadas como completadas") },
                    onClick = { taskViewModel.applySelectedTasksStatus(true) },
                    modifier = Modifier.padding(bottom = 8.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,

                )
                ExtendedFloatingActionButton(
                    text = { Text("Marcar como Pendientes") },
                    icon = { Icon(Icons.Filled.PendingActions, contentDescription = "Marcar seleccionadas como pendientes") },
                    onClick = { taskViewModel.applySelectedTasksStatus(false) },
                    modifier = Modifier.padding(bottom = 16.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,

                )
                FloatingActionButton(onClick = { navController.navigate("addTask") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar Tarea")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay tareas para mostrar con los filtros actuales.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        taskViewModel = taskViewModel,
                        isSelected = selectedTaskIds.contains(task.name)
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskScreen(navController: NavController, taskViewModel: TaskViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var deadlineInput by remember { mutableStateOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Nueva Tarea") },
                navigationIcon = {
                    // Botón para volver a la pantalla anterior
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la tarea") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Tipo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = deadlineInput,
                onValueChange = { newValue ->

                    deadlineInput = newValue
                },
                label = { Text("Fecha Límite (YYYY-MM-DD HH:MM)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )


            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    errorMessage = null
                    try {
                        val parsedDeadline = LocalDateTime.parse(deadlineInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        val newTask = taskViewModel.createTask(
                            name = name,
                            description = description,
                            type = type,
                            deadline = parsedDeadline
                        )
                        taskViewModel.addTask(newTask)

                        navController.popBackStack()

                    } catch (e: IllegalArgumentException) {

                        errorMessage = e.message
                    } catch (e: DateTimeParseException) {
                        errorMessage = "Formato de fecha u hora inválido. Use YYYY-MM-DD HH:MM (Ej: 2025-12-25 14:30)"
                    } catch (e: Exception) {
                        errorMessage = "Ocurrió un error inesperado: ${e.localizedMessage}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Tarea")
            }
        }
    }
}

