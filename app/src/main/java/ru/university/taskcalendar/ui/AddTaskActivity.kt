package ru.university.taskcalendar.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.university.taskcalendar.R
import ru.university.taskcalendar.model.Task
import ru.university.taskcalendar.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Экран формы: создание новой задачи или редактирование существующей.
 * Если в Intent передан TASK_ID — это редактирование, форма заполняется данными задачи.
 */
class AddTaskActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    private var editingTaskId: Int = NO_TASK_ID

    // Редактируемая задача (нужна, чтобы при сохранении не потерять поле isDone).
    private var editingTask: Task? = null

    // Заполнена ли форма данными задачи. Нужно, чтобы после поворота экрана
    // не затереть то, что пользователь уже успел изменить.
    private var isFormFilled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val timeEditText = findViewById<EditText>(R.id.timeEditText)
        val saveTaskButton = findViewById<Button>(R.id.saveTaskButton)

        isFormFilled = savedInstanceState?.getBoolean(STATE_FORM_FILLED) ?: false
        editingTaskId = intent.getIntExtra(EXTRA_TASK_ID, NO_TASK_ID)

        if (editingTaskId != NO_TASK_ID) {
            saveTaskButton.setText(R.string.button_save_changes)
            // Пока задача не загружена, сохранять нечего.
            saveTaskButton.isEnabled = false

            viewModel.selectTask(editingTaskId)
            viewModel.selectedTask.observe(this) { task ->
                if (task == null) {
                    Toast.makeText(this, R.string.toast_task_not_found, Toast.LENGTH_SHORT).show()
                    finish()
                    return@observe
                }

                editingTask = task
                saveTaskButton.isEnabled = true

                if (!isFormFilled) {
                    titleEditText.setText(task.title)
                    descriptionEditText.setText(task.description)
                    dateEditText.setText(task.date)
                    timeEditText.setText(task.time)
                    isFormFilled = true
                }
            }
        }

        saveTaskButton.setOnClickListener {

            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val time = timeEditText.text.toString().trim()

            if (title.isEmpty()) {
                titleEditText.error = getString(R.string.error_title_empty)
                titleEditText.requestFocus()
                return@setOnClickListener
            }

            if (date.isEmpty()) {
                dateEditText.error = getString(R.string.error_date_empty)
                dateEditText.requestFocus()
                return@setOnClickListener
            }

            if (time.isEmpty()) {
                timeEditText.error = getString(R.string.error_time_empty)
                timeEditText.requestFocus()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                dateEditText.error = getString(R.string.error_date_invalid)
                dateEditText.requestFocus()
                Toast.makeText(this, R.string.toast_date_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidTime(time)) {
                timeEditText.error = getString(R.string.error_time_invalid)
                timeEditText.requestFocus()
                Toast.makeText(this, R.string.toast_time_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val existingTask = editingTask
            if (existingTask == null) {
                // Режим создания
                viewModel.addTask(
                    Task(
                        title = title,
                        description = description,
                        date = date,
                        time = time
                    )
                )
                Toast.makeText(this, R.string.toast_task_saved, Toast.LENGTH_SHORT).show()
            } else {
                // Режим редактирования: меняем поля, id и isDone сохраняются
                viewModel.updateTask(
                    existingTask.copy(
                        title = title,
                        description = description,
                        date = date,
                        time = time
                    )
                )
                Toast.makeText(this, R.string.toast_task_updated, Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_FORM_FILLED, isFormFilled)
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

    private companion object {
        const val STATE_FORM_FILLED = "STATE_FORM_FILLED"
    }
}
