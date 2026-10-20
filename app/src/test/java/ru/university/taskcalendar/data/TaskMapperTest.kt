package ru.university.taskcalendar.data

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.university.taskcalendar.model.Task

/**
 * Обычный unit-тест (выполняется на компьютере, эмулятор не нужен):
 * преобразование между строкой таблицы (TaskEntity) и моделью экрана (Task).
 */
class TaskMapperTest {

    @Test
    fun entityToTask_copiesAllFields() {
        val entity = TaskEntity(
            id = 7,
            title = "Заголовок",
            description = "Описание",
            date = "05.10.2026",
            time = "18:00",
            isDone = true
        )

        val task = entity.toTask()

        assertEquals(7, task.id)
        assertEquals("Заголовок", task.title)
        assertEquals("Описание", task.description)
        assertEquals("05.10.2026", task.date)
        assertEquals("18:00", task.time)
        assertEquals(true, task.isDone)
    }

    @Test
    fun taskToEntity_copiesAllFields() {
        val task = Task(
            id = 3,
            title = "Задача",
            description = "Текст",
            date = "06.10.2026",
            time = "09:30",
            isDone = false
        )

        val entity = task.toEntity()

        assertEquals(3, entity.id)
        assertEquals("Задача", entity.title)
        assertEquals("Текст", entity.description)
        assertEquals("06.10.2026", entity.date)
        assertEquals("09:30", entity.time)
        assertEquals(false, entity.isDone)
    }

    @Test
    fun newTaskHasZeroId_soDatabaseCanGenerateIt() {
        val task = Task(title = "Новая", description = "", date = "05.10.2026", time = "10:00")

        assertEquals(0, task.toEntity().id)
    }

    @Test
    fun roundTrip_keepsTaskUnchanged() {
        val task = Task(5, "Туда-обратно", "Описание", "07.10.2026", "21:15", true)

        assertEquals(task, task.toEntity().toTask())
    }
}
