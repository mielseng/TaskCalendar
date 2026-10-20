package ru.university.taskcalendar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.university.taskcalendar.data.AppDatabase
import ru.university.taskcalendar.data.TaskRepository
import ru.university.taskcalendar.model.Task

/**
 * ViewModel хранит состояние экрана и логику работы с задачами.
 *
 * Она переживает пересоздание Activity (например, поворот экрана), поэтому
 * данные не загружаются заново. Экраны только подписываются на LiveData
 * через observe() и вызывают методы addTask / updateTask / deleteTask.
 *
 * AndroidViewModel нужна, чтобы получить Application и создать базу данных,
 * при этом Activity вообще не знают про AppDatabase и TaskDao.
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(
        AppDatabase.getDatabase(application).taskDao()
    )

    /** Список всех задач для главного экрана. */
    val tasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    private val selectedTaskId = MutableLiveData<Int>()

    /**
     * Выбранная задача (для экранов деталей и редактирования).
     * Меняется вместе с selectedTaskId; пока id тот же, запрос к базе не повторяется.
     */
    val selectedTask: LiveData<Task?> = selectedTaskId.switchMap { id ->
        repository.observeTask(id).asLiveData()
    }

    /** Выбрать задачу по id. Повторный вызов с тем же id (например, после поворота) ничего не делает. */
    fun selectTask(id: Int) {
        if (selectedTaskId.value != id) {
            selectedTaskId.value = id
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch { repository.addTask(task) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }
}
