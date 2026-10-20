package ru.university.taskcalendar.data

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Явные тесты работы Room (этап 3): вставка, чтение, обновление, удаление.
 *
 * Тесты запускаются на эмуляторе/устройстве и используют базу ТОЛЬКО В ПАМЯТИ,
 * поэтому реальные данные приложения они не затрагивают.
 * Каждый шаг пишется в Logcat с тегом DB_TEST — это и есть доказательство
 * того, что данные сохраняются в базу и читаются из неё.
 *
 * Запуск: ПКМ по классу -> Run 'TaskDaoTest'. Фильтр Logcat: tag:DB_TEST
 */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        taskDao = database.taskDao()
        Log.d(TAG, "---- БД в памяти создана, таблица tasks пуста ----")
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertOneTask_isSavedAndReadBack() {
        runBlocking {
            val newId = taskDao.insert(newTask("Тест вставки", "05.10.2026", "18:00"))
            Log.d(TAG, "INSERT: база выдала id=$newId")

            val saved = taskDao.getTaskById(newId.toInt())
            Log.d(TAG, "READ: по id=$newId из базы прочитано: $saved")

            assertTrue("База должна выдать id больше нуля", newId > 0)
            assertNotNull("Сохранённая задача должна читаться из базы", saved)
            assertEquals("Тест вставки", saved?.title)
            assertEquals("05.10.2026", saved?.date)
            assertEquals("18:00", saved?.time)
            assertEquals(false, saved?.isDone)
        }
    }

    @Test
    fun insertSeveralTasks_allAreReturnedSortedByDateAndTime() {
        runBlocking {
            taskDao.insert(newTask("Третья", "06.10.2026", "10:00"))
            taskDao.insert(newTask("Первая", "05.10.2026", "09:00"))
            taskDao.insert(newTask("Вторая", "05.10.2026", "18:30"))
            Log.d(TAG, "INSERT: добавлено 3 задачи")

            val all = taskDao.getAllTasks()
            Log.d(TAG, "READ: в таблице tasks сейчас ${all.size} задач(и):")
            all.forEach {
                Log.d(TAG, "   #${it.id} ${it.date} ${it.time} \"${it.title}\" isDone=${it.isDone}")
            }

            assertEquals(3, all.size)
            assertEquals(listOf("Первая", "Вторая", "Третья"), all.map { it.title })
        }
    }

    @Test
    fun getTasksByDate_returnsOnlyTasksOfThatDay() {
        runBlocking {
            taskDao.insert(newTask("Понедельник 1", "05.10.2026", "09:00"))
            taskDao.insert(newTask("Понедельник 2", "05.10.2026", "12:00"))
            taskDao.insert(newTask("Вторник", "06.10.2026", "09:00"))

            val monday = taskDao.getTasksByDate("05.10.2026")
            Log.d(TAG, "READ по дате 05.10.2026: ${monday.map { it.title }}")

            assertEquals(listOf("Понедельник 1", "Понедельник 2"), monday.map { it.title })
        }
    }

    @Test
    fun updateTask_changesSavedData() {
        runBlocking {
            val id = taskDao.insert(newTask("Задача", "05.10.2026", "18:00")).toInt()
            val original = taskDao.getTaskById(id)!!

            taskDao.update(original.copy(title = "Задача (изменена)", isDone = true))

            val updated = taskDao.getTaskById(id)
            Log.d(TAG, "UPDATE: было $original, стало $updated")

            assertEquals("Задача (изменена)", updated?.title)
            assertEquals(true, updated?.isDone)
        }
    }

    @Test
    fun deleteTask_removesItFromDatabase() {
        runBlocking {
            val id = taskDao.insert(newTask("Удалить меня", "05.10.2026", "18:00")).toInt()
            val saved = taskDao.getTaskById(id)!!

            taskDao.delete(saved)

            Log.d(TAG, "DELETE: задача id=$id удалена, осталось в таблице: ${taskDao.getAllTasks().size}")

            assertNull(taskDao.getTaskById(id))
            assertTrue(taskDao.getAllTasks().isEmpty())
        }
    }

    @Test
    fun observeAllTasks_flowEmitsCurrentContentOfTable() {
        runBlocking {
            taskDao.insert(newTask("Для Flow", "05.10.2026", "18:00"))

            val fromFlow = taskDao.observeAllTasks().first()
            Log.d(TAG, "FLOW: первая выдача Flow содержит ${fromFlow.size} задач(у)")

            assertEquals(listOf("Для Flow"), fromFlow.map { it.title })
        }
    }

    private fun newTask(title: String, date: String, time: String) = TaskEntity(
        title = title,
        description = "Описание: $title",
        date = date,
        time = time,
        isDone = false
    )

    private companion object {
        const val TAG = "DB_TEST"
    }
}
