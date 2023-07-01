package com.dicoding.todoapp.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.detail.DetailTaskViewModel
import com.dicoding.todoapp.ui.list.TaskActivity
import com.dicoding.todoapp.utils.DatePickerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener {
    private var dueDateMillis: Long = System.currentTimeMillis()

    private lateinit var addTaskViewModel: AddTaskViewModel

    private lateinit var addEdTitle: EditText
    private lateinit var addEdDescription: EditText
    private lateinit var addTvDueDate: TextView
    private lateinit var addIbDueDate: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        supportActionBar?.title = getString(R.string.add_task)

        val factory = ViewModelFactory.getInstance(this)
        addTaskViewModel = ViewModelProvider(this, factory)[AddTaskViewModel::class.java]

        addEdTitle = findViewById(R.id.add_ed_title)
        addEdDescription = findViewById(R.id.add_ed_description)
        addTvDueDate = findViewById(R.id.add_tv_due_date)
        addIbDueDate = findViewById(R.id.add_ib_due_date)

        addIbDueDate.setOnClickListener {
            showDatePicker()
        }

        addTvDueDate.setOnClickListener {
            showDatePicker()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                // XTODO 12 : Create AddTaskViewModel and insert new task to database
                val title = addEdTitle.text.toString()
                val desc = addEdDescription.text.toString()

                if (title.isNotBlank()) {
                    if (desc.isNotBlank()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            addTaskViewModel.insertTask(
                                Task(
                                    title = title,
                                    description = desc,
                                    dueDateMillis = dueDateMillis
                                )
                            )
                        }
                        val intent = Intent(this@AddTaskActivity, TaskActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDatePicker() {
        val dialogFragment = DatePickerFragment()
        dialogFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.add_tv_due_date).text = dateFormat.format(calendar.time)

        dueDateMillis = calendar.timeInMillis
    }
}