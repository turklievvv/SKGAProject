package com.example.skga.presentation.adminPage.scheduleManage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import com.google.android.material.button.MaterialButton
import domain.entity.ScheduleItem

class ScheduleManageAdapter(private var scheduleList: List<ScheduleItem>) : RecyclerView.Adapter<ScheduleManageAdapter.ScheduleViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_lesson_item, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ScheduleViewHolder,
        position: Int
    ) {
        holder.lessonName.text = scheduleList[position].lessonName
        holder.lessonTime.text = "${scheduleList[position].lessonStartTime}-${scheduleList[position].lessonEndTime}"
        holder.lessonRoom.text = scheduleList[position].lessonClassRoom
        holder.scheduleGroup.text = scheduleList[position].group
        if (scheduleList[position].weekType == 0) {
            holder.weekType.visibility = View.GONE
        }
        val weekType = when (scheduleList[position].weekType) {
            0 -> ""
            1 -> "Числитель"
            2 -> "Знаменатель"
            else -> "Числитель"
        }
        if (weekType == "") {
            holder.weekType.visibility = View.GONE
        } else {
            holder.weekType.text = weekType
        }

        val subgroup = when (scheduleList[position].subGroup) {
            0 -> ""
            1 -> "1 подгруппа"
            2 -> "2 подгруппа"
            else -> "1 подгруппа"
        }
        if (subgroup == "") {
            holder.subgroupLesson.visibility = View.GONE
        } else {
            holder.subgroupLesson.text = subgroup
        }
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lessonName = view.findViewById<TextView>(R.id.scheduleLessonName)
        val lessonTime = view.findViewById<TextView>(R.id.scheduleLessonTime)
        val lessonRoom = view.findViewById<TextView>(R.id.scheduleLessonRoom)
        val scheduleGroup = view.findViewById<TextView>(R.id.scheduleGroupName)
        val subgroupLesson = view.findViewById<MaterialButton>(R.id.subgroupTV)
        val weekType = view.findViewById<MaterialButton>(R.id.weekTypeTv)

    }


}