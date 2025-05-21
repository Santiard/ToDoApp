package com.example.todoapp.data

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime

class TaskViewModel : ViewModel(){

    private val _tasks = MutableStateFlow<MutableList<Task>>(mutableListOf())
    var tasks: StateFlow<List<Task>> = _tasks.asStateFlow()


    fun addTask(newTask: Task) {
        _tasks.value.add(newTask) // Agg nueva tarea a la lista mutable
        _tasks.value = _tasks.value // Forza la actualizacion
    }

    fun removeTask(task: Task) {
        _tasks.value.remove(task)
        _tasks.value = _tasks.value // Forzamos la emisión
    }

    fun getFilteredTasks(completed: Boolean): List<Task> {
        return tasks.value.filter { it.status == completed }
    }
}

