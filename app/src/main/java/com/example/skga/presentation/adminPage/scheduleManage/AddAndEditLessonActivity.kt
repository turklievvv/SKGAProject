package com.example.skga.presentation.adminPage.scheduleManage

import android.app.Activity
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

        bindingAutoCompleteTextViews()

        val existingLesson = intent.getParcelableExtra(SCHEDULE_ITEM, ScheduleItem::class.java)
        if (existingLesson != null) {
            viewModel.setEditMode(true)
            fillForm(existingLesson)
            binding.saveBtn.text = "Сохранить изменения"
        } else {
            binding.saveBtn.text = "Добавить занятие"
        }

        binding.saveBtn.setOnClickListener {
            val lesson = getLessonFromForm(existingLesson) ?: return@setOnClickListener
            viewModel.saveLesson(lesson)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.saveBtn.isEnabled = !isLoading
        }

        viewModel.saveResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Занятие сохранено", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_GROUP_ID, binding.groupAutoCompleteTextView.text.toString())
                }
                setResult(Activity.RESULT_OK,resultIntent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Ошибка: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fillForm(lesson: ScheduleItem) {
        val days = resources.getStringArray(R.array.days_of_week)
        // ИСПРАВЛЕНИЕ 4: Добавлен параметр false, чтобы не фильтровать выпадающие списки при инициализации
        binding.dayOfWeekAutoCompleteTV.setText(days.getOrNull(lesson.dayOfWeek - 1) ?: "", false)
        binding.lessonNameET.setText(lesson.lessonName)
        binding.lessonNumberET.setText(lesson.lessonNumber.toString())
        binding.lessonRoomET.setText(lesson.lessonClassRoom)
        binding.lessonStartTimeET.setText(lesson.lessonStartTime)
        binding.lessonEndTimeET.setText(lesson.lessonEndTime)
        binding.groupAutoCompleteTextView.setText(lesson.group, false)
        binding.lessonTeacherAutoCompleteTV.setText(lesson.lessonTeacherFullName, false)

        val weekTypes = resources.getStringArray(R.array.week_type)
        binding.weekTypeAutoCompleteTV.setText(weekTypes.getOrNull(lesson.weekType) ?: "", false)

        val subgroups = resources.getStringArray(R.array.subgroup_list)
        binding.subgroupAutoCompleteTV.setText(subgroups.getOrNull(lesson.subGroup) ?: "", false)
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
            val teacherNames = it.map { teacher ->
                "${teacher.lastName} ${teacher.firstName} ${teacher.middleName}"
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

        // ИСПРАВЛЕНИЕ 3: Исправлена опечатка (было клик на subgroup вместо weekType)
        binding.weekTypeAutoCompleteTV.setOnClickListener {
            binding.weekTypeAutoCompleteTV.showDropDown()
        }
    }

    private fun showTimePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timerPickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val time = String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute)
                editText.setText(time)
            },
            hour,
            minute,
            true
        )
        timerPickerDialog.show()
    }

    private fun getLessonFromForm(schedule: ScheduleItem?): ScheduleItem? {
        val dayOfWeekIndex = resources.getStringArray(R.array.days_of_week)
            .indexOf(binding.dayOfWeekAutoCompleteTV.text.toString()) + 1

        val weekTypeIndex = resources.getStringArray(R.array.week_type)
            .indexOf(binding.weekTypeAutoCompleteTV.text.toString())

        val subgroupIndex = resources.getStringArray(R.array.subgroup_list)
            .indexOf(binding.subgroupAutoCompleteTV.text.toString())

        val selectedTeacherText = binding.lessonTeacherAutoCompleteTV.text.toString()
        val teacher = viewModel.teacherList.value
            ?.find { "${it.lastName} ${it.firstName} ${it.middleName}" == selectedTeacherText }

        // Проверяем заполнение текстовых полей
        if (binding.lessonNameET.text.isNullOrBlank() ||
            binding.groupAutoCompleteTextView.text.isNullOrBlank() ||
            binding.lessonStartTimeET.text.isNullOrBlank() ||
            binding.lessonEndTimeET.text.isNullOrBlank() ||
            selectedTeacherText.isBlank()
        ) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return null
        }

        // ИСПРАВЛЕНИЕ 2: Безопасное определение ID и имен учителя, если список из сети еще не догрузился
        val teacherId: String
        val teacherFullName: String
        val teacherShortName: String

        if (teacher != null) {
            teacherId = teacher.id
            teacherFullName = "${teacher.lastName} ${teacher.firstName} ${teacher.middleName}"
            teacherShortName = "${teacher.lastName} ${teacher.firstName.firstOrNull()}. ${teacher.middleName.firstOrNull()}."
        } else if (schedule != null && selectedTeacherText == schedule.lessonTeacherFullName) {
            // Если мы редактируем и имя совпадает со старым — берем старые ID и имена напрямую из объекта
            teacherId = schedule.lessonTeacherId
            teacherFullName = schedule.lessonTeacherFullName
            teacherShortName = schedule.lessonTeacherShortName
        } else {
            Toast.makeText(this, "Выберите преподавателя из списка", Toast.LENGTH_SHORT).show()
            return null
        }

        return ScheduleItem(
            id = schedule?.id ?: UUID.randomUUID().toString(),
            lessonName = binding.lessonNameET.text.toString(),
            lessonNumber = binding.lessonNumberET.text.toString().toIntOrNull() ?: 1,
            lessonClassRoom = binding.lessonRoomET.text.toString(),
            dayOfWeek = dayOfWeekIndex,
            weekType = weekTypeIndex,
            subGroup = subgroupIndex,
            group = binding.groupAutoCompleteTextView.text.toString(),
            lessonStartTime = binding.lessonStartTimeET.text.toString(),
            lessonEndTime = binding.lessonEndTimeET.text.toString(),
            lessonTeacherId = teacherId,
            lessonTeacherFullName = teacherFullName,
            lessonTeacherShortName = teacherShortName
        )
    }

    companion object {
        const val SCHEDULE_ITEM = "schedule_item"

        const val EXTRA_GROUP_ID = "extra_group_id"

        fun newIntent(context: Context, schedule: ScheduleItem?): Intent {
            val intent = Intent(context, AddAndEditLessonActivity::class.java)
            if (schedule != null) {
                intent.apply { putExtra(SCHEDULE_ITEM, schedule) }
            }
            return intent
        }
    }
}