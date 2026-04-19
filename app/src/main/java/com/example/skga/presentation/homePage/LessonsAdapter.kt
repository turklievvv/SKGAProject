package com.example.skga.presentation.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import domain.entity.ScheduleItem

class LessonsAdapter(private val lessons: List<ScheduleItem>) :
    RecyclerView.Adapter<LessonsAdapter.LessonViewHolder>() {

    class LessonViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val queue: TextView = view.findViewById(R.id.queueNumber)
        val tvEndTime: TextView = view.findViewById(R.id.lessonEndTime)

        val tvStartTime: TextView = view.findViewById(R.id.lessonStartTime)
        val lessonName: TextView = view.findViewById(R.id.lessonName)
        val tvTeacher: TextView = view.findViewById(R.id.teacherName)
        val tvRoom: TextView = view.findViewById(R.id.lessonRoom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lesson_item, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]

        holder.queue.text = lesson.lessonNumber.toString()
        holder.tvStartTime.text = lesson.lessonStartTime
        holder.tvEndTime.text = lesson.lessonEndTime
        holder.lessonName.text = lesson.lessonName
        holder.tvTeacher.text = lesson.lessonTeacherFullName
        holder.tvRoom.text = "Ауд: ${lesson.lessonClassRoom}"
    }

    override fun getItemCount(): Int = lessons.size
}