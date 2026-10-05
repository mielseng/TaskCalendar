package ru.university.taskcalendar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private var editingTaskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val timeEditText = findViewById<EditText>(R.id.timeEditText)
        val saveTaskButton = findViewById<Button>(R.id.saveTaskButton)

        val database = AppDatabase.getDatabase(this)
        val taskDao = database.taskDao()

        editingTaskId = intent.getIntExtra("TASK_ID", -1)

        if (editingTaskId != -1) {

            saveTaskButton.text = "Сохранить изменения"

            CoroutineScope(Dispatchers.IO).launch {

                val task = taskDao.getTaskById(editingTaskId)

                runOnUiThread {

                    if (task != null) {

                        titleEditText.setText(task.title)
                        descriptionEditText.setText(task.description)
                        dateEditText.setText(task.date)
                        timeEditText.setText(task.time)
                    }
                }
            }
        }

        saveTaskButton.setOnClickListener {

            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val time = timeEditText.text.toString().trim()

            if (title.isEmpty()) {
                titleEditText.error = "Введите название задачи"
                titleEditText.requestFocus()
                return@setOnClickListener
            }

            if (date.isEmpty()) {
                dateEditText.error = "Введите дату"
                dateEditText.requestFocus()
                return@setOnClickListener
            }

            if (time.isEmpty()) {
                timeEditText.error = "Введите время"
                timeEditText.requestFocus()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {

                dateEditText.error =
                    "Введите корректную дату в формате ДД.ММ.ГГГГ"

                dateEditText.requestFocus()

                Toast.makeText(
                    this,
                    "Некорректная дата",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (!isValidTime(time)) {

                timeEditText.error =
                    "Введите корректное время в формате ЧЧ:ММ"

                timeEditText.requestFocus()

                Toast.makeText(
                    this,
                    "Некорректное время. Например: 18:30",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {

                if (editingTaskId == -1) {

                    val task = TaskEntity(
                        title = title,
                        description = description,
                        date = date,
                        time = time,
                        isDone = false
                    )

                    taskDao.insert(task)

                } else {

                    val oldTask = taskDao.getTaskById(editingTaskId)

                    if (oldTask != null) {

                        val updatedTask = oldTask.copy(
                            title = title,
                            description = description,
                            date = date,
                            time = time
                        )

                        taskDao.update(updatedTask)
                    }
                }

                runOnUiThread {

                    Toast.makeText(
                        this@AddTaskActivity,
                        if (editingTaskId == -1) {
                            "Задача сохранена"
                        } else {
                            "Задача изменена"
                        },
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }

    private fun isValidDate(date: String): Boolean {

        if (!date.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}"))) {
            return false
        }

        return try {

            val formatter = SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            )

            formatter.isLenient = false
            formatter.parse(date)

            true

        } catch (e: Exception) {
            false
        }
    }

    private fun isValidTime(time: String): Boolean {

        if (!time.matches(Regex("\\d{2}:\\d{2}"))) {
            return false
        }

        val parts = time.split(":")

        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()

        return hours in 0..23 && minutes in 0..59
    }
}