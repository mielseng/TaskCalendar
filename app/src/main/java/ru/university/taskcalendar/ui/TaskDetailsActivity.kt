package ru.university.taskcalendar.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.university.taskcalendar.R
import ru.university.taskcalendar.model.Task
import ru.university.taskcalendar.viewmodel.TaskViewModel

/**
 * Экран деталей задачи.
 *
 * Данные приходят из ViewModel через observe(): после «Отметить как выполненную»
 * или после редактирования экран обновляется сам, без recreate().
 */
class TaskDetailsActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    // Последняя полученная версия задачи — на неё действуют кнопки.
    private var currentTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        val taskId = intent.getIntExtra(EXTRA_TASK_ID, NO_TASK_ID)
        if (taskId == NO_TASK_ID) {
            Toast.makeText(this, R.string.toast_task_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val taskTitleDetails = findViewById<TextView>(R.id.taskTitleDetails)
        val taskDescriptionDetails = findViewById<TextView>(R.id.taskDescriptionDetails)
        val taskDateDetails = findViewById<TextView>(R.id.taskDateDetails)
        val taskTimeDetails = findViewById<TextView>(R.id.taskTimeDetails)
        val taskStatusDetails = findViewById<TextView>(R.id.taskStatusDetails)

        val editTaskButton = findViewById<Button>(R.id.editTaskButton)
        val completeTaskButton = findViewById<Button>(R.id.completeTaskButton)
        val deleteTaskButton = findViewById<Button>(R.id.deleteTaskButton)

        editTaskButton.setOnClickListener {
            currentTask?.let { task ->
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra(EXTRA_TASK_ID, task.id)
                startActivity(intent)
            }
        }

        completeTaskButton.setOnClickListener {
            currentTask?.let { task ->
                viewModel.updateTask(task.copy(isDone = !task.isDone))
            }
        }

        deleteTaskButton.setOnClickListener {
            currentTask?.let { task ->
                viewModel.deleteTask(task)
                Toast.makeText(this, R.string.toast_task_deleted, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.selectTask(taskId)
        viewModel.selectedTask.observe(this) { task ->
            if (task == null) {
                // Если задача уже была показана, значит её только что удалили — просто закрываемся.
                if (currentTask == null) {
                    Toast.makeText(this, R.string.toast_task_not_found, Toast.LENGTH_SHORT).show()
                }
                finish()
                return@observe
            }

            currentTask = task

            taskTitleDetails.text = task.title
            taskDescriptionDetails.text =
                task.description.ifEmpty { getString(R.string.details_no_description) }
            taskDateDetails.text = getString(R.string.details_date, task.date)
            taskTimeDetails.text = getString(R.string.details_time, task.time)
            taskStatusDetails.setText(
                if (task.isDone) R.string.details_status_done else R.string.details_status_not_done
            )
            completeTaskButton.setText(
                if (task.isDone) R.string.button_return_to_not_done else R.string.button_mark_done
            )
        }
    }
}
