package com.example.skga.presentation.adminPage.teacherPage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skga.R
import com.example.skga.databinding.ActivityTeacherInfoBinding
import com.example.skga.presentation.adminPage.scheduleManage.ScheduleManageAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import domain.entity.TeacherItem

class TeacherInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityTeacherInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeacherInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val teacherItem = intent.getParcelableExtra(TEACHER_ITEM, TeacherItem::class.java)
        Log.d("DEBUG_TEACHER", "teacherItem получил: $teacherItem")
        if (teacherItem == null) {
            Log.e("TeacherDetail", "Ошибка: TeacherItem пришел null из Intent")
            Toast.makeText(this, "Не удалось загрузить данные преподавателя", Toast.LENGTH_LONG)
                .show()
            finish()
            return
        }

        setupUI(teacherItem)
        bindingButtons(teacherItem)
        setupRecyclerViews(teacherItem)
    }

    private fun setupUI(teacherItem: TeacherItem) {
        binding.teacherLessonsCount.text = teacherItem.teacherLessons.size.toString()
        binding.teacherHours.text = teacherItem.totalHours.toString()
        binding.teacherGroupsCount.text = teacherItem.teacherGroups.size.toString()
        binding.teacherMailTV.text = teacherItem.userProfile.email
        binding.teacherPhoneNumberTV.text = teacherItem.userProfile.phone
        val faculty = when (teacherItem.userProfile.facultyId) {
            1 -> "Институт Экономики"
            2 -> "Институт Цифровых Технологий"
            3 -> "Инженерный институт"
            else -> ""

        }
        binding.teacherFaculty.text = faculty
    }

    private fun bindingButtons(teacherItem: TeacherItem) {
        binding.teacherSendMailBtn.setOnClickListener {
            val email = teacherItem.userProfile.email

            if (email.isEmpty()) {
                Toast.makeText(it.context, "Email преподавателя не указан", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val subject = Uri.encode("Учебная часть (Администрация)")
            val uriString = "mailto:$email?subject=$subject"

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(uriString)
            }

            try {
                this.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Почтовое приложение не найдено", Toast.LENGTH_LONG).show()
            }
        }

        binding.teacherCallButton.setOnClickListener {
            val phone = teacherItem.userProfile.phone
            if (phone.isEmpty()) {
                Toast.makeText(it.context, "Номер преподавателя не указан", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            this.startActivity(intent)
        }
    }

    private fun setupRecyclerViews(teacherItem: TeacherItem) {

        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }

        val flexboxLayoutManager1 = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }
        binding.teacherLessonsRV.layoutManager = flexboxLayoutManager
        binding.teacherLessonsRV.adapter =
            TeacherInfoAdapter(teacherItem.teacherLessons.map { it.lessonName })


        binding.teacherGroupsRV.layoutManager = flexboxLayoutManager1
        binding.teacherGroupsRV.adapter =
            TeacherInfoAdapter(teacherItem.teacherGroups)


        binding.teacherScheduleRV.layoutManager = LinearLayoutManager(this)
        binding.teacherScheduleRV.adapter = ScheduleManageAdapter(teacherItem.teacherLessons, true)
        binding.TeacherCountOfLessonInCalendar.text = "${teacherItem.teacherLessons.size} занятий"
    }

    companion object {
        const val TEACHER_ITEM = "teacher_item"
        fun newIntent(context: Context, teacherItem: TeacherItem): Intent {
            return Intent(context, TeacherInfoActivity::class.java).apply {
                putExtra(TEACHER_ITEM, teacherItem)
            }
        }
    }
}