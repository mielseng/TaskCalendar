package ru.university.taskcalendar.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.university.taskcalendar.R
import ru.university.taskcalendar.viewmodel.TaskViewModel

/**
 * Главный экран: список задач.
 *
 * Activity только показывает данные. Список приходит из TaskViewModel через observe(),
 * поэтому он обновляется сам при любом изменении базы (добавили, изменили, удалили задачу).
 * Здесь нет ни обращений к базе, ни ручной перезагрузки списка в onResume().
 */
class MainActivity : AppCompatActivity() {

    // by viewModels() выдаёт ViewModel, привязанную к этому экрану.
    // После поворота экрана будет возвращён тот же самый объект.
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taskAdapter = TaskAdapter { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra(EXTRA_TASK_ID, task.id)
            startActivity(intent)
        }

        val taskRecyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        val addTaskButton = findViewById<FloatingActionButton>(R.id.addTaskButton)
        addTaskButton.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // Подписка на список: код в фигурных скобках выполняется
        // при каждом изменении списка задач в базе.
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }
}
