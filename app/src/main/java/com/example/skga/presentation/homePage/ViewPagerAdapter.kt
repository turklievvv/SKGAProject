package com.example.skga.presentation.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import domain.entity.ScheduleItem

class ViewPagerAdapter(private val lessonsByDay: Map<Int, List<ScheduleItem>>) :
    RecyclerView.Adapter<ViewPagerAdapter.DayViewHolder>() {

    override fun getItemCount(): Int = 14 // Дни недели

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSchedule)
        val emptyText: TextView = view.findViewById(R.id.emptyText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_day_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayNumber = position + 1 // Пн = 1
        val lessonsForThisDay = lessonsByDay[dayNumber] ?: emptyList()

        if (lessonsForThisDay.isEmpty()) {
            holder.emptyText.visibility = View.VISIBLE
            holder.recyclerView.visibility = View.GONE
        } else {
            holder.emptyText.visibility = View.GONE
            holder.recyclerView.visibility = View.VISIBLE
            // А ТУТ САМОЕ ВАЖНОЕ: Внутренний адаптер для списка пар
            val lessonsAdapter = LessonsAdapter(lessonsForThisDay)
            holder.recyclerView.adapter = lessonsAdapter
        }
    }
}