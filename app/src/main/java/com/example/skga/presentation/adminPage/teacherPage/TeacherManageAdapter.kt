package com.example.skga.presentation.adminPage.teacherPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import com.google.android.material.button.MaterialButton
import domain.entity.TeacherItem

class TeacherManageAdapter(
    private val teacherList: List<TeacherItem>,
    private val onCallClick: (String) -> Unit,
    private val onEmailClick: (String) -> Unit
) : RecyclerView.Adapter<TeacherManageAdapter.TeacherViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teacher_item, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TeacherViewHolder,
        position: Int
    ) {
        val item = teacherList[position]
        val context = holder.itemView.context
        holder.itemView.setOnClickListener {
            context.startActivity(TeacherInfoActivity.newIntent(context,item))
        }
        holder.teacherName.text =
            "${item.userProfile.lastName} ${item.userProfile.firstName.firstOrNull()}. ${item.userProfile.middleName.firstOrNull()}."
        holder.teacherGroupCount.text = context.getString(R.string.teacher_groups, item.teacherGroups.size)
        holder.teacherHours.text = context.getString(R.string.teacher_hours, item.totalHours)
        holder.teacherLessonsCount.text =
            context.getString(R.string.teacher_lessons, item.lessonCount)
        holder.teacherLessonWeekCount.text =
            context.getString(R.string.teacher_lesson_count, item.weekLessonCount)
        holder.callButton.setOnClickListener {
            val phone = item.userProfile.phone // или как называется поле в DTO/Entity
            if (!phone.isNullOrBlank()) {
                onCallClick(phone)
            } else {
                Toast.makeText(context, "Номер телефона не указан", Toast.LENGTH_SHORT).show()
            }
        }
        holder.emailMessageBtn.setOnClickListener {
            val email = item.userProfile.email // Берем email из профиля
            if (!email.isNullOrBlank()) {
                onEmailClick(email) // Передаем адрес во фрагмент
            } else {
                Toast.makeText(context, "Email не указан", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return teacherList.size
    }

    class TeacherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teacherName = view.findViewById<TextView>(R.id.teacherNameTv)
        val teacherLessonsCount = view.findViewById<TextView>(R.id.teacherLessons)
        val teacherLessonWeekCount = view.findViewById<TextView>(R.id.teacherLessonsCountTv)
        val teacherGroupCount = view.findViewById<TextView>(R.id.teacherGroupsCount)
        val teacherHours = view.findViewById<TextView>(R.id.teacherHoursInWeek)

        val callButton = view.findViewById<MaterialButton>(R.id.callButton)
        val emailMessageBtn = view.findViewById<MaterialButton>(R.id.emailMessageButton)
    }

}