package com.example.skga.presentation.adminPage.teacherPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skga.R
import com.example.skga.databinding.ActivityTeacherAddEditBinding
import domain.entity.FacultyItem
import domain.entity.TeacherItem
import domain.entity.UserProfile
import java.util.UUID

class TeacherAddEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityTeacherAddEditBinding
    private var facultyList: List<FacultyItem> = emptyList()

    private val viewModel: TeacherAddEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeacherAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val teacherItem = intent.getParcelableExtra(
            TEACHER_ITEM,
            TeacherItem::class.java
        )
        if (teacherItem == null) {
            binding.sendInviteBtn.visibility = View.VISIBLE
            binding.saveBtn.visibility = View.INVISIBLE
        } else {
            setInfo(teacherItem)
            binding.sendInviteBtn.visibility = View.INVISIBLE
            binding.saveBtn.visibility = View.VISIBLE
        }
        viewModel.getFaculties()
        addGroupAdapter()
        binding.sendInviteBtn.setOnClickListener {
            val teacherProfile = getProfile()
            viewModel.inviteTeacher(
                teacherProfile
            )
            Toast.makeText(
                this,
                "Отправлено приглашения для ${teacherProfile.lastName} ${teacherProfile.firstName}",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun setInfo(teacherItem: TeacherItem) {
        binding.teacherLastNameET.setText(teacherItem.userProfile.lastName)
        binding.teacherNameET.setText(teacherItem.userProfile.firstName)
        binding.teacherMiddleNameEt.setText(teacherItem.userProfile.middleName)

        val faculty = when (teacherItem.userProfile.facultyId) {
            1 -> "Институт Экономики"
            2 -> "Институт Цифровых Технологий"
            3 -> "Инженерный институт"
            else -> ""

        }
        binding.teacherFacultyAC.setText(faculty)

        binding.teacherPhoneET.setText(teacherItem.userProfile.phone)
        binding.teahcerEmailET.setText(teacherItem.userProfile.email)
    }

    private fun getProfile(): UserProfile {
        val generatedTeacherId = UUID.randomUUID().toString()
        val faculty = facultyList.find { it.name == binding.teacherFacultyAC.text.toString() }
        Log.d("REPO_DEBUG", "Сгенерирован клиентский ID для учителя: $generatedTeacherId")
        return UserProfile(
            id = generatedTeacherId,
            lastName = binding.teacherLastNameET.text.toString(),
            firstName = binding.teacherNameET.text.toString(),
            middleName = binding.teacherMiddleNameEt.text.toString(),
            email = binding.teahcerEmailET.text.toString(),
            phone = binding.teacherPhoneET.text.toString(),
            facultyId = faculty?.id,
            role = "teacher",
            group = null,
            avatarUrl = null,
            subgroup = null,
            course = null
        )
    }

    private fun addGroupAdapter() {
        val autoCompleteTextViewGroup = binding.teacherFacultyAC
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, mutableListOf<String>()
        )
        autoCompleteTextViewGroup.setAdapter(adapter)
        viewModel.allFaculties.observe(this) { faculties ->
            adapter.clear()
            adapter.addAll(faculties.map { it.name })
            adapter.notifyDataSetChanged()
            facultyList = faculties
        }
    }

    companion object {
        const val TEACHER_ITEM = "teacher_item"
        fun newIntent(context: Context, teacherItem: TeacherItem?): Intent {
            return Intent(context, TeacherAddEditActivity::class.java).apply {
                if (teacherItem != null) {
                    putExtra(TEACHER_ITEM, teacherItem)
                }
            }
        }
    }
}