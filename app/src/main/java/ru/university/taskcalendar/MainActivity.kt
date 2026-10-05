package ru.university.taskcalendar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskRecyclerView = findViewById(R.id.taskRecyclerView)

        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        val database = AppDatabase.getDatabase(this)
        taskDao = database.taskDao()

        val addTaskButton = findViewById<FloatingActionButton>(R.id.addTaskButton)

        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {

        CoroutineScope(Dispatchers.IO).launch {

            val tasksFromDatabase = taskDao.getAllTasks()

            val tasks = tasksFromDatabase.map { taskEntity ->
                Task(
                    id = taskEntity.id,
                    title = taskEntity.title,
                    description = taskEntity.description,
                    date = taskEntity.date,
                    time = taskEntity.time,
                    isDone = taskEntity.isDone
                )
            }

            runOnUiThread {

                taskRecyclerView.adapter = TaskAdapter(tasks) { task ->

                    val intent = Intent(
                        this@MainActivity,
                        TaskDetailsActivity::class.java
                    )

                    intent.putExtra("TASK_ID", task.id)
                    startActivity(intent)
                }
            }
        }
    }
}