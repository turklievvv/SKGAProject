package com.example.skga.presentation.adminPage.teacherPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R

class TeacherInfoAdapter(
    private val list: List<String>
) : RecyclerView.Adapter<TeacherInfoAdapter.TeacherLessonsAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherLessonsAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subject_item, parent, false)
        return TeacherLessonsAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherLessonsAdapterViewHolder, position: Int) {
        holder.tvSubjectName.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class TeacherLessonsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
    }
}