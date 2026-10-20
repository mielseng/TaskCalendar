package ru.university.taskcalendar.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.university.taskcalendar.model.Task

/**
 * Repository — единственная точка доступа к данным о задачах.
 *
 * ViewModel ничего не знает о Room, Entity и SQL: она просит у Repository
 * задачи и получает обычные объекты [Task]. Все обращения к [TaskDao]
 * в приложении идут только через этот класс.
 *
 * Сообщения в Logcat (тег "TaskRepository") показывают, что данные
 * реально записываются в базу и читаются из неё.
 */
class TaskRepository(private val taskDao: TaskDao) {

    /** Все задачи. Новый список приходит автоматически при любом изменении таблицы. */
    val allTasks: Flow<List<Task>> = taskDao.observeAllTasks()
        .onEach { entities ->
            Log.d(TAG, "READ: из базы прочитано задач: ${entities.size} -> " +
                    entities.joinToString { "#${it.id} \"${it.title}\"" })
        }
        .map { entities -> entities.map { it.toTask() } }

    /** Одна задача по id. Эмитит null, если такой задачи нет (или её удалили). */
    fun observeTask(id: Int): Flow<Task?> = taskDao.observeTaskById(id)
        .map { entity -> entity?.toTask() }

    suspend fun addTask(task: Task) {
        withContext(WRITE_CONTEXT) {
            val newId = taskDao.insert(task.toEntity())
            Log.d(TAG, "INSERT: id=$newId, \"${task.title}\", ${task.date} ${task.time}")
        }
    }

    suspend fun updateTask(task: Task) {
        withContext(WRITE_CONTEXT) {
            taskDao.update(task.toEntity())
            Log.d(TAG, "UPDATE: id=${task.id}, \"${task.title}\", isDone=${task.isDone}")
        }
    }

    suspend fun deleteTask(task: Task) {
        withContext(WRITE_CONTEXT) {
            taskDao.delete(task.toEntity())
            Log.d(TAG, "DELETE: id=${task.id}, \"${task.title}\"")
        }
    }

    private companion object {
        const val TAG = "TaskRepository"

        /**
         * Запись выполняется в потоке ввода-вывода (Dispatchers.IO), а NonCancellable
         * гарантирует, что она завершится, даже если экран закрылся сразу после
         * сохранения (при закрытии экрана ViewModel уничтожается вместе со своим scope).
         */
        val WRITE_CONTEXT = Dispatchers.IO + NonCancellable
    }
}
