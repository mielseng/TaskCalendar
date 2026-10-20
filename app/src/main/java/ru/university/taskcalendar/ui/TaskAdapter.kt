package ru.university.taskcalendar.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.university.taskcalendar.R
import ru.university.taskcalendar.model.Task

/**
 * Адаптер списка задач.
 *
 * ListAdapter — это RecyclerView.Adapter, который сам хранит список и сравнивает
 * старый и новый списки через DiffUtil. Activity достаточно вызвать submitList(...):
 * изменившиеся карточки обновятся сами, вручную дёргать notify...() не нужно.
 */
class TaskAdapter(
    private val onTaskClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskDate: TextView = itemView.findViewById(R.id.taskDate)
        val taskStatus: TextView = itemView.findViewById(R.id.taskStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)

        holder.taskTitle.text = task.title
        holder.taskDate.text = holder.itemView.context
            .getString(R.string.task_date_time, task.date, task.time)
        holder.taskStatus.setText(
            if (task.isDone) R.string.task_status_done else R.string.task_status_not_done
        )

        holder.itemView.setOnClickListener { onTaskClick(task) }
    }
}

/** Правила сравнения для DiffUtil: та же ли это задача и изменилось ли её содержимое. */
private object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
        oldItem == newItem
}
