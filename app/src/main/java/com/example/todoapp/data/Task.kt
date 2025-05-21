package com.example.todoapp.data

import android.content.Context
import android.widget.Toast
import java.time.LocalDate
import java.time.LocalDateTime


data class Task(

    val name: String,
    val description : String,
    val type: String,
    var status: Boolean,
    val addDate: LocalDate,
    val deadline: LocalDateTime,

)

fun getTaskByName(taskName: String, taskList: List<Task>): Task? {
    return taskList.find { task -> task.name == taskName }
}


fun createTask(name: String, description: String, type: String, addDate: LocalDate, deadline: LocalDateTime): Task {
    if (name.isBlank()) {
        throw IllegalArgumentException("El nombre de la tarea no puede estar vacío.")
    }
    if (deadline.equals(null)) {
        throw IllegalArgumentException("La fecha limite de la tarea no puede estar vacía.")
    }

    return Task(name = name, description = description, type = type, status = false, addDate = addDate, deadline =deadline)
}

fun checkTask(task: Task){
    task.status=true;
}
