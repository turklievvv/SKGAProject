package com.example.skga.presentation.adminPage.scheduleManage

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skga.R
import com.example.skga.databinding.ActivityAddAndEditLessonBinding
import com.google.android.material.textfield.TextInputEditText
import domain.entity.ScheduleItem
import java.util.Calendar
import java.util.UUID


class AddAndEditLessonActivity : AppCompatActivity() {

    private val viewModel: AddAndEditLessonViewModel by viewModels()
    lateinit var binding: ActivityAddAndEditLessonBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
        val existingLesson = intent.getParcelableExtra(SCHEDULE_ITEM, ScheduleItem::class.java)
        if (existingLesson != null) {
            viewModel.setEditMode(true)
            fillForm(existingLesson)
            binding.saveBtn.text = "Сохранить изменения"
        } else {
            binding.saveBtn.text = "Добавить занятие"
        }

        binding.saveBtn.setOnClickListener {
            val lesson = getLessonFromForm() ?: return@setOnClickListener
            viewModel.saveLesson(lesson)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.saveBtn.isEnabled = !isLoading
        }

        viewModel.saveResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Занятие сохранено", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Ошибка: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        bindingAutoCompleteTextViews()
    }

    private fun fillForm(lesson: ScheduleItem) {
        val days = resources.getStringArray(R.array.days_of_week)
        binding.dayOfWeekAutoCompleteTV.setText(days.getOrNull(lesson.dayOfWeek - 1) ?: "")
        binding.lessonNameET.setText(lesson.lessonName)
        binding.lessonNumberET.setText(lesson.lessonNumber.toString())
        binding.lessonRoomET.setText(lesson.lessonClassRoom)
        binding.lessonStartTimeET.setText(lesson.lessonStartTime)
        binding.lessonEndTimeET.setText(lesson.lessonEndTime)
        binding.groupAutoCompleteTextView.setText(lesson.group)

        val weekTypes = resources.getStringArray(R.array.week_type)
        binding.weekTypeAutoCompleteTV.setText(weekTypes.getOrNull(lesson.weekType) ?: "")

        val subgroups = resources.getStringArray(R.array.subgroup_list)
        binding.subgroupAutoCompleteTV.setText(subgroups.getOrNull(lesson.subGroup) ?: "")
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
            val teacherNames = it.map {
                "${it.lastName} ${it.firstName} ${it.middleName}"
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, teacherNames)
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

    private fun getLessonFromForm(): ScheduleItem? {
        val dayOfWeekIndex = resources.getStringArray(R.array.days_of_week)
            .indexOf(binding.dayOfWeekAutoCompleteTV.text.toString()) + 1

        val weekTypeIndex = resources.getStringArray(R.array.week_type)
            .indexOf(binding.weekTypeAutoCompleteTV.text.toString())

        val subgroupIndex = resources.getStringArray(R.array.subgroup_list)
            .indexOf(binding.subgroupAutoCompleteTV.text.toString())

        val teacher = viewModel.teacherList.value
            ?.find { "${it.lastName} ${it.firstName} ${it.middleName}" == binding.lessonTeacherAutoCompleteTV.text.toString() }

        if (binding.lessonNameET.text.isNullOrBlank() ||
            binding.groupAutoCompleteTextView.text.isNullOrBlank() ||
            binding.lessonStartTimeET.text.isNullOrBlank() ||
            binding.lessonEndTimeET.text.isNullOrBlank() ||
            teacher == null
        ) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return null
        }

        return ScheduleItem(
            id = UUID.randomUUID().toString(),
            lessonName = binding.lessonNameET.text.toString(),
            lessonNumber = binding.lessonNumberET.text.toString().toIntOrNull() ?: 1,
            lessonClassRoom = binding.lessonRoomET.text.toString(),
            dayOfWeek = dayOfWeekIndex,
            weekType = weekTypeIndex,
            subGroup = subgroupIndex,
            group = binding.groupAutoCompleteTextView.text.toString(),
            lessonStartTime = binding.lessonStartTimeET.text.toString(),
            lessonEndTime = binding.lessonEndTimeET.text.toString(),
            lessonTeacherId = teacher.id,
            lessonTeacherFullName = "${teacher.lastName} ${teacher.firstName} ${teacher.middleName}",
            lessonTeacherShortName = "${teacher.lastName} ${teacher.firstName.firstOrNull()}. ${teacher.middleName.firstOrNull()}."
        )
    }


    companion object {
        const val SCHEDULE_ITEM = "schedule_item"

        fun newIntent(context: Context, schedule: ScheduleItem?): Intent {
            val intent = Intent(context, AddAndEditLessonActivity::class.java)
            if (schedule != null) {
                intent.apply { putExtra(SCHEDULE_ITEM, schedule) }
            }
            return intent
        }


    }
}