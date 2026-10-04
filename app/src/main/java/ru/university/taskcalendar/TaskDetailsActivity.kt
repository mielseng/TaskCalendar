package ru.university.taskcalendar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        val taskId = intent.getIntExtra("TASK_ID", -1)

        val taskIdText = findViewById<TextView>(R.id.taskIdText)
        taskIdText.text = "ID задачи: $taskId"
    }
}