package ru.university.taskcalendar.data

import ru.university.taskcalendar.model.Task

/** Строка таблицы -> модель для экрана. */
fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time,
    isDone = isDone
)

/** Модель с экрана -> строка таблицы. */
fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time,
    isDone = isDone
)
