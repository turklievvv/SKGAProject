package com.example.skga.presentation.adminPage.scheduleManage

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skga.R
import com.example.skga.databinding.ActivityAddAndEditLessonBinding
import com.google.android.material.textfield.TextInputEditText
import domain.entity.ScheduleItem
import java.util.Calendar


class AddAndEditLessonActivity : AppCompatActivity() {

    private val viewModel: AddAndEditLessonViewModel by viewModels()
    lateinit var binding: ActivityAddAndEditLessonBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddAndEditLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bindingAutoCompleteTextViews()

    }

    private fun bindingAutoCompleteTextViews() {
        val days = resources.getStringArray(R.array.days_of_week)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, days)
        binding.dayOfWeekAutoCompleteTV.setAdapter(adapter)
        binding.dayOfWeekAutoCompleteTV.setOnClickListener {
            binding.dayOfWeekAutoCompleteTV.showDropDown()
        }

        viewModel.loadGroups()
        viewModel.groupList.observe(this) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, it)
            binding.groupAutoCompleteTextView.setAdapter(adapter)
        }

        binding.lessonStartTimeET.setOnClickListener {
            showTimePickerDialog(binding.lessonStartTimeET)
        }

        binding.lessonEndTimeET.setOnClickListener {
            showTimePickerDialog(binding.lessonEndTimeET)
        }

        viewModel.loadTeachers()
        viewModel.teacherList.observe(this) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, it)
            binding.lessonTeacherAutoCompleteTV.setAdapter(adapter)
        }

        val subgroups = resources.getStringArray(R.array.subgroup_list)
        val adapterSubgroup = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subgroups)
        binding.subgroupAutoCompleteTV.setAdapter(adapterSubgroup)
        binding.subgroupAutoCompleteTV.setOnClickListener {
            binding.subgroupAutoCompleteTV.showDropDown()
        }

        val weekType = resources.getStringArray(R.array.week_type)
        val adapterWeekType = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, weekType)
        binding.weekTypeAutoCompleteTV.setAdapter(adapterWeekType)
        binding.subgroupAutoCompleteTV.setOnClickListener {
            binding.subgroupAutoCompleteTV.showDropDown()
        }


    }

    private fun showTimePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timerPickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val time =
                    String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute)
                editText.setText(time)
            },
            hour,
            minute,
            true
        )
        timerPickerDialog.show()
    }


    private fun putSchedule(schedule: ScheduleItem) {
        val intent = Intent().apply {
            putExtra(SCHEDULE_ITEM, schedule)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val SCHEDULE_ITEM = "schedule_item"


    }
}