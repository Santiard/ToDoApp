package com.example.todoapp.data


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month


@RequiresApi(Build.VERSION_CODES.O)
class TaskViewModel : ViewModel(){

    private val _tasks = MutableStateFlow<MutableList<Task>>(mutableListOf())
    var tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    //estado del filtro
    private val _filterType = MutableStateFlow(TaskFilter.ALL.name)
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _selectedTaskIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedTaskIds: StateFlow<Set<String>> = _selectedTaskIds.asStateFlow()

    //ids de las tareas seleccionadas en el checkbox
    val displayedTasks: StateFlow<List<Task>> = combine(
        _tasks,
        _searchText,
        _filterType
    ) { tasksList, search, filterString ->
        tasksList
            .filter { task ->
                val searchLower = search.lowercase()
                task.name.lowercase().contains(searchLower) ||
                        task.description.lowercase().contains(searchLower) ||
                        task.type.lowercase().contains(searchLower) ||
                        task.deadline.toString().lowercase().contains(searchLower)
            }
            .filter { task ->
                when (filterString) {
                    TaskFilter.ALL.name -> true
                    TaskFilter.PENDING.name -> !task.status
                    TaskFilter.COMPLETED.name -> task.status
                    else -> true
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTask(newTask: Task) {
        _tasks.value.add(newTask)
        _tasks.value = _tasks.value
    }

    fun removeTask(task: Task) {
        _tasks.value.remove(task)
        _tasks.value = _tasks.value
    }

    fun getFilteredTasks(completed: Boolean): List<Task> {
        return tasks.value.filter { it.status == completed }
    }
    enum class TaskFilter {
        ALL, PENDING, COMPLETED
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun updateFilterType(filter: TaskFilter) {
        _filterType.value = filter.name
    }

    //maneja las funciones de la seleccion de checkbox
    fun toggleTaskSelection(taskId: String) {
        _selectedTaskIds.update { currentSelected ->
            if (currentSelected.contains(taskId)) {
                currentSelected - taskId // Desmarcar
            } else {
                currentSelected + taskId // Marcar
            }
        }
    }
    fun checkTask(task: Task, newStatus: Boolean) {

        if (task.status != newStatus) {
            task.status = newStatus
            _tasks.value = _tasks.value.toMutableList()
        }
    }

    fun applySelectedTasksStatus(newStatus: Boolean) {
        _tasks.update { currentTasks ->
            currentTasks.map { task ->
                if (_selectedTaskIds.value.contains(task.name)) {
                    task.copy(status = newStatus)

                } else {
                    task
                }
            }.toMutableList()
        }
        _selectedTaskIds.value = emptySet()
    }

    fun createTask(name: String, description: String, type: String, deadline: LocalDateTime): Task {
        if (name.isBlank()) {
            throw IllegalArgumentException("El nombre de la tarea no puede estar vacío.")
        }
        return Task(
            name = name,
            description = description,
            type = type,
            status = false,
            addDate = LocalDate.now(),
            deadline = deadline
        )
    }

    init {
        addTask(createTask("Comprar víveres", "Leche, pan, huevos", "Hogar", LocalDateTime.of(2025, Month.MAY, 22, 18, 0)))
        addTask(createTask("Preparar presentación", "Reunión del lunes", "Trabajo", LocalDateTime.of(2025, Month.MAY, 26, 9, 30)))
        addTask(createTask("Llamar a María", "Para coordinar la cena", "Personal", LocalDateTime.of(2025, Month.MAY, 19, 20, 0))) // Pasada
        checkTask(tasks.value[2], true)
        addTask(createTask("Leer libro Kotlin", "Capítulo 3", "Educación", LocalDateTime.of(2025, Month.JUNE, 1, 12, 0)))
        addTask(createTask("Ejercicio mañana", "Rutina de pesas", "Salud", LocalDateTime.of(LocalDate.of(2025, Month.MAY, 21), LocalTime.of(7,0))))
    }

}

