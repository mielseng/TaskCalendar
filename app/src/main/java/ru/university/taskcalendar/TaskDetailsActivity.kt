package ru.university.taskcalendar

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        val taskTitleDetails = findViewById<TextView>(R.id.taskTitleDetails)
        val taskDescriptionDetails =
            findViewById<TextView>(R.id.taskDescriptionDetails)
        val taskDateDetails = findViewById<TextView>(R.id.taskDateDetails)
        val taskTimeDetails = findViewById<TextView>(R.id.taskTimeDetails)
        val taskStatusDetails = findViewById<TextView>(R.id.taskStatusDetails)

        val editTaskButton = findViewById<Button>(R.id.editTaskButton)
        val completeTaskButton = findViewById<Button>(R.id.completeTaskButton)
        val deleteTaskButton = findViewById<Button>(R.id.deleteTaskButton)

        val taskId = intent.getIntExtra("TASK_ID", -1)

        if (taskId == -1) {
            Toast.makeText(
                this,
                "Задача не найдена",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val database = AppDatabase.getDatabase(this)
        val taskDao = database.taskDao()

        CoroutineScope(Dispatchers.IO).launch {

            val task = taskDao.getTaskById(taskId)

            runOnUiThread {

                if (task == null) {

                    Toast.makeText(
                        this@TaskDetailsActivity,
                        "Задача не найдена",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    taskTitleDetails.text = task.title

                    taskDescriptionDetails.text =
                        if (task.description.isEmpty()) {
                            "Описание отсутствует"
                        } else {
                            task.description
                        }

                    taskDateDetails.text = "Дата: ${task.date}"
                    taskTimeDetails.text = "Время: ${task.time}"

                    taskStatusDetails.text =
                        if (task.isDone) {
                            "Статус: Выполнена"
                        } else {
                            "Статус: Не выполнена"
                        }

                    if (task.isDone) {
                        completeTaskButton.text = "Вернуть в невыполненные"
                    } else {
                        completeTaskButton.text = "Отметить как выполненную"
                    }

                    editTaskButton.setOnClickListener {

                        val intent = android.content.Intent(
                            this@TaskDetailsActivity,
                            AddTaskActivity::class.java
                        )

                        intent.putExtra("TASK_ID", task.id)
                        startActivity(intent)
                    }

                    completeTaskButton.setOnClickListener {

                        CoroutineScope(Dispatchers.IO).launch {

                            val updatedTask = task.copy(
                                isDone = !task.isDone
                            )

                            taskDao.update(updatedTask)

                            runOnUiThread {
                                recreate()
                            }
                        }
                    }

                    deleteTaskButton.setOnClickListener {

                        CoroutineScope(Dispatchers.IO).launch {

                            taskDao.delete(task)

                            runOnUiThread {

                                Toast.makeText(
                                    this@TaskDetailsActivity,
                                    "Задача удалена",
                                    Toast.LENGTH_SHORT
                                ).show()

                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}