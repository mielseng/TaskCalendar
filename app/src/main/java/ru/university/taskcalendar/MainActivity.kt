package ru.university.taskcalendar

import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taskRecyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)

        val tasks = listOf(
            Task(
                1,
                "Отдохнуть с друзьями после рабочей недели",
                "Бильярд, банька и т.д.",
                "04.10.2026"
            ),
            Task(
                2,
                "Начать создавать поэтапно андройд приложение",
                "Повторить основные конструкции языка",
                "05.10.2026"
            ),
            Task(
                3,
                "Сделать лабораторную работу",
                "Выполнить задания лабораторной работы",
                "06.10.2026"
            ),
            Task(
                4,
                "Подготовиться к занятию",
                "Повторить материал предыдущей темы",
                "07.10.2026"
            ),
            Task(
                5,
                "Повторить материал",
                "Повторить изученные темы",
                "08.10.2026"
            )
        )

        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        taskRecyclerView.adapter = TaskAdapter(tasks) { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)

            val addTaskButton = findViewById<FloatingActionButton>(R.id.addTaskButton)

            addTaskButton.setOnClickListener {
                val intent = Intent(this, AddTaskActivity::class.java)
                startActivity(intent)
            }
        }
    }
}