package ru.university.taskcalendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: TaskEntity)
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
}