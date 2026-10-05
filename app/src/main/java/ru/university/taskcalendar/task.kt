package ru.university.taskcalendar

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val isDone: Boolean
)