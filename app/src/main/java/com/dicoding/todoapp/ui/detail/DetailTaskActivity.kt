package com.dicoding.todoapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskActivity
import com.dicoding.todoapp.utils.DateConverter

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var detailTaskViewModel: DetailTaskViewModel

    private lateinit var detailEdTitle: EditText
    private lateinit var detailEdDescription: EditText
    private lateinit var detailEdDueDate: EditText
    private lateinit var btnDeleteTask: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // XTODO 11 : Show detail task and implement delete action
        val taskId = intent.getIntExtra("TASK_ID", -1)

        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        detailTaskViewModel.setTaskId(taskId)

        detailEdTitle = findViewById(R.id.detail_ed_title)
        detailEdDescription = findViewById(R.id.detail_ed_description)
        detailEdDueDate = findViewById(R.id.detail_ed_due_date)
        btnDeleteTask = findViewById(R.id.btn_delete_task)

        detailTaskViewModel.task.observe(this) { task ->
            if (task != null){
                detailEdTitle.setText(task.title)
                detailEdDescription.setText(task.description)
                val dueDate = DateConverter.convertMillisToString(task.dueDateMillis)
                detailEdDueDate.setText(dueDate)
            }
        }

        btnDeleteTask.setOnClickListener {
            detailTaskViewModel.deleteTask()
            val intent = Intent(this@DetailTaskActivity, TaskActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}