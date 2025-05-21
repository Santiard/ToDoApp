package com.example.todoapp.data


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




