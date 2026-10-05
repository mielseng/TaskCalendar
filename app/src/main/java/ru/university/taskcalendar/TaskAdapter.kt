package ru.university.taskcalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView =
            itemView.findViewById(R.id.taskTitle)
        val taskDate: TextView =
            itemView.findViewById(R.id.taskDate)
        val taskStatus: TextView =
            itemView.findViewById(R.id.taskStatus)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }
    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        val task = tasks[position]
        holder.taskTitle.text = task.title
        holder.taskDate.text = "${task.date} ${task.time}"
        holder.taskStatus.text =
            if (task.isDone) {
                "Выполнена"
            } else {
                "Не выполнена"
            }
        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }
    }
    override fun getItemCount(): Int {
        return tasks.size
    }
}