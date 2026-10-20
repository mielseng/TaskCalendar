package ru.university.taskcalendar.model

/**
 * Модель задачи, с которой работают ViewModel и экраны (слой Model для UI).
 *
 * Это обычный Kotlin-класс без зависимостей от Room. Таблица базы данных
 * описывается отдельным классом [ru.university.taskcalendar.data.TaskEntity],
 * а преобразование между ними выполняет Repository.
 *
 * id = 0 означает «задача ещё не сохранена»: настоящий id выдаст база (autoGenerate).
 */
data class Task(
    val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val isDone: Boolean = false
)
