package com.example.skga.presentation.adminPage.scheduleManage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skga.R
import com.example.skga.databinding.ActivityScheduleManageBinding
import java.util.Calendar

class ScheduleManageActivity : AppCompatActivity() {

    private val viewModel: ScheduleManageViewModel by viewModels()

    lateinit var binding: ActivityScheduleManageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScheduleManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadCachedSchedule()
        loadGroups()
        clickCurrentDay()
        bindingGetSchedule()
        bindingToggleGroup()
        bindingBottomMenu()


        val recyclerView = binding.recyclerViewScheduleAdmin
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.scheduleList.observe(this) {
            recyclerView.adapter = ScheduleManageAdapter(it)
            if (it.isEmpty()) {
                binding.todayNoHaveLessonsTV.visibility = View.VISIBLE
            } else {
                binding.todayNoHaveLessonsTV.visibility = View.GONE
            }
        }
    }

    private fun bindingToggleGroup() {
        binding.buttonToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val day = when (checkedId) {
                    R.id.btnMon -> 1
                    R.id.btnTue -> 2
                    R.id.btnWed -> 3
                    R.id.btnThur -> 4
                    R.id.btnFri -> 5
                    R.id.btnSatur -> 6
                    R.id.btnSun -> 7
                    else -> 1
                }
                val dayString = when (checkedId) {
                    R.id.btnMon -> "Понедельник"
                    R.id.btnTue -> "Вторник"
                    R.id.btnWed -> "Среда"
                    R.id.btnThur -> "Четверг"
                    R.id.btnFri -> "Пятница"
                    R.id.btnSatur -> "Суббота"
                    R.id.btnSun -> "Воскресенье"
                    else -> "Понедельник"
                }
                binding.dayOfWeekTV.text = dayString
                binding.todayNoHaveLessonsTV.text =
                    getString(R.string.no_lessons_on_date, dayString.lowercase())
                viewModel.filterByDay(day)
            }
        }

    }

    private fun bindingGetSchedule() {
        val autoCompleteTextView = binding.autoCompleteTextView
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val group = parent.getItemAtPosition(position).toString()
                val checkedId = binding.buttonToggleGroup.checkedButtonId
                val day = when (checkedId) {
                    R.id.btnMon -> 1
                    R.id.btnTue -> 2
                    R.id.btnWed -> 3
                    R.id.btnThur -> 4
                    R.id.btnFri -> 5
                    R.id.btnSatur -> 6
                    R.id.btnSun -> 7
                    else -> 1
                }
                viewModel.getGroupSchedule(group, day)
            }
    }

    private fun clickCurrentDay() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val buttonIdToClick = when (dayOfWeek) {
            Calendar.MONDAY -> R.id.btnMon
            Calendar.TUESDAY -> R.id.btnTue
            Calendar.WEDNESDAY -> R.id.btnWed
            Calendar.THURSDAY -> R.id.btnThur
            Calendar.FRIDAY -> R.id.btnFri
            Calendar.SATURDAY -> R.id.btnSatur
            Calendar.SUNDAY -> R.id.btnSun
            else -> R.id.btnMon
        }

        binding.dayOfWeekTV.text = viewModel.getDayOfWeekStringFromCalendar()
        binding.todayNoHaveLessonsTV.text =
            getString(
                R.string.no_lessons_on_date,
                viewModel.getDayOfWeekStringFromCalendar().lowercase()
            )
        binding.buttonToggleGroup.check(buttonIdToClick)
    }

    private fun loadCachedSchedule() {
        viewModel.loadScheduleFromCache(viewModel.getDayOfWeekIntFromCalendar())
    }

    private fun bindingBottomMenu() {
        viewModel.group.observe(this) {
            if (it != null) {
                binding.currentGroupTV.text = it
            } else
                binding.currentGroupTV.text = ""
        }
        viewModel.scheduleList.observe(this) {
            if (it.isNotEmpty()) {
                binding.countLessonTV.text = it.size.toString()
            } else {
                binding.countLessonTV.text = "0"
            }
        }
    }

    private fun loadGroups() {
        viewModel.getGroups()
        val autoCompleteTextView = binding.autoCompleteTextView
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, mutableListOf<String>()
        )
        autoCompleteTextView.setAdapter(adapter)
        viewModel.groupList.observe(this) { it ->
            adapter.clear()
            adapter.addAll(it)
            adapter.filter.filter(null)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ScheduleManageActivity::class.java)
        }
    }
}