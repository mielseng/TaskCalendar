package ru.university.taskcalendar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица `tasks` в базе данных Room: каждое поле класса — столбец таблицы.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val isDone: Boolean
)
