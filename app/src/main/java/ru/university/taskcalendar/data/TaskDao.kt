package ru.university.taskcalendar.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object — все операции с таблицей `tasks`.
 *
 * suspend-методы Room выполняет не в главном потоке.
 * Методы, возвращающие Flow, не помечаются suspend: Flow сам выполняет запрос
 * в фоне и присылает новый результат каждый раз, когда таблица меняется.
 */
@Dao
interface TaskDao {

    /** Возвращает id, который база присвоила новой задаче. */
    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY date, time")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY time")
    suspend fun getTasksByDate(date: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): TaskEntity?

    // ---- Этап 4: наблюдаемые запросы (на них подписывается ViewModel) ----

    @Query("SELECT * FROM tasks ORDER BY date, time")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun observeTaskById(id: Int): Flow<TaskEntity?>
}
