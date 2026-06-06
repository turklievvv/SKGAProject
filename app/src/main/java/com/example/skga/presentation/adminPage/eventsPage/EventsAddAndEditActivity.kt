package com.example.skga.presentation.adminPage.eventsPage

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skga.R
import com.example.skga.databinding.ActivityEventsAddAndEditActivtyBinding
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import domain.entity.EventItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class EventsAddAndEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityEventsAddAndEditActivtyBinding
    private val viewModel: EventsAddAndEditViewModel by viewModels()
    private var currentEventItem: EventItem? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEventsAddAndEditActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Сохраняем интент во внешнюю переменную, чтобы иметь к нему доступ при отрисовке чипов
        currentEventItem = intent.getParcelableExtra(EVENT_EXTRA, EventItem::class.java)

        if (currentEventItem != null) {
            viewModel.setEditMode(currentEventItem!!)  // ← сообщаем ViewModel о режиме редактирования
            setInfo(currentEventItem!!)
            binding.bottomCreateEventBtn.text = "Сохранить изменения"  // ← меняем текст кнопки
        } else {
            binding.bottomCreateEventBtn.text = "Создать событие"
        }

        viewModel.filteredGroups.observe(this) { groups ->
            updateChips(groups)
        }

        observeLoading()
        addOtherChips()
        addFacultiesChip() // Единственное место, где теперь создаются чипы факультетов
        chipCheckedFaculties()
        bindingEventDateAndTime()

        binding.bottomCreateEventBtn.setOnClickListener {
            if (eventIsNotBlank()) {
                viewModel.createEvent(getEventItem())
            } else {
                Toast.makeText(this, "Заполните необходимые поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getEventItem(): EventItem {
        val generatedEventId = currentEventItem?.id ?: UUID.randomUUID().toString()
        val othersChip = getOthersChip()
        return EventItem(
            id = generatedEventId,
            eventName = binding.eventNameET.text.toString(),
            eventDescription = binding.eventDescriptionET.text.toString(),
            eventDate = binding.eventDateET.text.toString(),
            eventType = binding.eventTypeET.text.toString(),
            eventTime = "${binding.startTimeET.text}-${binding.endTimeET.text}",
            eventIsTeachers = "Все учителя" in othersChip,
            eventIsGlobal = "Весь институт" in othersChip,
            eventLocation = binding.eventLocationET.text.toString(),
            eventIsActual = true,
            eventGroups = getGroupsChip(),
            eventFaculties = getFacultiesChip(),
        )
    }

    private fun bindingEventDateAndTime() {
        binding.eventDateET.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату события")
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selectionInMs ->
                val timeZoneUtc = TimeZone.getTimeZone("UTC")
                val offset = TimeZone.getDefault().getOffset(selectionInMs) - timeZoneUtc.getOffset(
                    selectionInMs
                )
                val localDate = Date(selectionInMs - offset)

                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = formatter.format(localDate)

                binding.eventDateET.setText(formattedDate)
            }
            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }

        binding.startTimeET.setOnClickListener {
            showTimePickerDialog(binding.startTimeET, "Выберите время начала")
        }
        binding.endTimeET.setOnClickListener {
            showTimePickerDialog(binding.endTimeET, "Выберите время конца")
        }
    }

    private fun eventIsNotBlank(): Boolean {
        val hasFaculties = getFacultiesChip().isNotEmpty()
        val hasGroups = getGroupsChip().isNotEmpty()
        val hasOthers = getOthersChip().isNotEmpty()
        val hasAnyTarget = hasFaculties || hasGroups || hasOthers

        return binding.eventNameET.text.toString().isNotBlank() &&
                binding.eventDescriptionET.text.toString().isNotBlank() &&
                binding.eventDateET.text.toString().isNotBlank() &&
                binding.eventTypeET.text.toString().isNotBlank() &&
                binding.eventLocationET.text.toString().isNotBlank() &&
                hasAnyTarget
    }

    private fun addOtherChips() {
        val availablePeoples = listOf("Весь институт", "Все учителя")
        val chipGroup = binding.othersChipGroup
        chipGroup.removeAllViews()

        availablePeoples.forEach { others ->
            val chip = Chip(this).apply {
                text = others
                isCheckable = true
                setEnsureMinTouchTargetSize(false)
            }
            chipGroup.addView(chip)
        }
    }

    private fun chipCheckedFaculties() {
        binding.facultiesChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedIds = getFacultiesChip()
                if (selectedIds.isNotEmpty()) {
                    viewModel.filterGroupsByMultipleFaculties(selectedIds)
                }
            } else {
                // Если реально ВСЕ галочки были сняты вручную — возвращаем полный список групп
                val selectedIds = getFacultiesChip()
                if (selectedIds.isEmpty()) {
                    viewModel.showAllGroups()
                }
            }
        }
    }

    private fun updateChips(groups: List<String>) {
        binding.groupsChipGroup.removeAllViews()
        groups.forEach { group ->
            val chip = Chip(this).apply {
                text = group
                isCheckable = true
                // Если мы в режиме редактирования, восстанавливаем выбор групп
                isChecked = currentEventItem?.eventGroups?.contains(group) ?: false
            }
            binding.groupsChipGroup.addView(chip)
        }
    }

    private fun addFacultiesChip() {
        viewModel.facultyList.observe(this) { faculties ->
            val chipGroup = binding.facultiesChipGroup
            chipGroup.removeAllViews()

            faculties.forEach { faculty ->
                val chip = Chip(this).apply {
                    contentDescription = faculty.id.toString()
                    text = faculty.name
                    isCheckable = true
                    setEnsureMinTouchTargetSize(false)
                    // Восстанавливаем выбор факультета, если открыто редактирование существующего ивента
                    isChecked = currentEventItem?.eventFaculties?.contains(faculty.id) ?: false
                }
                chipGroup.addView(chip)
            }
        }
    }

    private fun getOthersChip(): List<String> {
        val selectedOtherChips = mutableListOf<String>()
        for (i in 0 until binding.othersChipGroup.childCount) {
            val chip = binding.othersChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedOtherChips.add(chip.text.toString())
            }
        }
        return selectedOtherChips
    }

    private fun getGroupsChip(): List<String> {
        val selectedGroupsChips = mutableListOf<String>()
        for (i in 0 until binding.groupsChipGroup.childCount) {
            val chip = binding.groupsChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedGroupsChips.add(chip.text.toString())
            }
        }
        return selectedGroupsChips
    }

    private fun getFacultiesChip(): List<Int> {
        val selectedFacultiesChips = mutableListOf<Int>()
        for (i in 0 until binding.facultiesChipGroup.childCount) {
            val chip = binding.facultiesChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                val facultyId = chip.contentDescription?.toString()?.toIntOrNull()
                if (facultyId != null) {
                    selectedFacultiesChips.add(facultyId)
                    Log.d("REPO_DEBUG", "Факультет: $facultyId")
                }
            }
        }
        return selectedFacultiesChips
    }

    private fun setInfo(eventItem: EventItem) {
        binding.eventNameET.setText(eventItem.eventName)
        binding.eventLocationET.setText(eventItem.eventLocation)
        binding.eventDateET.setText(eventItem.eventDate)
        binding.eventTypeET.setText(eventItem.eventType)
        binding.eventDescriptionET.setText(eventItem.eventDescription)

        // Восстановление времени
        if (eventItem.eventTime.contains("-")) {
            val times = eventItem.eventTime.split("-")
            if (times.size == 2) {
                binding.startTimeET.setText(times[0])
                binding.endTimeET.setText(times[1])
            }
        }

        // Обновляем состояние "Других" чипов на основе пришедшего ивента
        for (i in 0 until binding.othersChipGroup.childCount) {
            val chip = binding.othersChipGroup.getChildAt(i) as Chip
            if (chip.text.toString() == "Все учителя") {
                chip.isChecked = eventItem.eventIsTeachers
            }
            if (chip.text.toString() == "Весь институт") {
                chip.isChecked = eventItem.eventIsGlobal
            }
        }
    }

    private fun showTimePickerDialog(targetEditText: EditText, title: String) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(title)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val formattedTime =
                String.format(Locale.getDefault(), "%02d:%02d", timePicker.hour, timePicker.minute)
            targetEditText.setText(formattedTime)
        }
        timePicker.show(supportFragmentManager, "MATERIAL_TIME_PICKER")
    }

    private fun observeLoading() {
        viewModel.isLoading.observe(this) {
            binding.topProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.saveStatus.observe(this) {
            if (it) {
                Toast.makeText(this, "Событие успешно создано", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Ошибка при создании события", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val EVENT_EXTRA = "event_extra"
        fun newIntent(context: Context, eventItem: EventItem?): Intent {
            return Intent(context, EventsAddAndEditActivity::class.java).apply {
                if (eventItem != null) {
                    putExtra(EVENT_EXTRA, eventItem)
                }
            }
        }
    }
}