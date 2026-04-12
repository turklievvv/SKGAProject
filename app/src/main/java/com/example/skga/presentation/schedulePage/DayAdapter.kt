package com.example.skga.presentation.schedulePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import com.example.skga.presentation.homePage.LessonsAdapter
import com.example.skga.presentation.homePage.ViewPagerAdapter
import domain.entity.DayConfig
import domain.entity.ScheduleItem

class DayAdapter(
    private val days: List<DayConfig>,
    private val allLessons: List<ScheduleItem>
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    override fun getItemCount(): Int = days.size

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSchedule)
        val emptyText: TextView = view.findViewById(R.id.emptyText)
        val dateTitle: TextView = view.findViewById(R.id.todayDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val context = holder.itemView.context
        val dayConfig = days[position]

        val weekTypeLabel = when (dayConfig.currentWeekType) {
            1 -> context.getString(R.string.week_type_numerator)
            2 -> context.getString(R.string.week_type_denominator)
            else -> context.getString(R.string.week_type_always)
        }
        holder.dateTitle.text = context.getString(
            R.string.title_format,
            dayConfig.dayNameShort,
            dayConfig.dateText,
            weekTypeLabel
        )

        val lessonsForThisDay = allLessons.filter { lesson ->
            // 1. Проверка дня
            val isCorrectDay = lesson.dayOfWeek == dayConfig.dayOfWeek

            // 2. Проверка недели: показываем если (урок всегда(0)) ИЛИ (урок совпадает с текущей(1 или 2))
            val isCorrectWeek = lesson.weekType == 0 || lesson.weekType == dayConfig.currentWeekType

            // 3. Проверка подгруппы: показываем если (урок для всех(0)) ИЛИ (урок совпадает с нашей(1 или 2))
            isCorrectDay && isCorrectWeek
        }.sortedBy { it.lessonNumber }

        if (lessonsForThisDay.isEmpty()) {
            holder.emptyText.visibility = View.VISIBLE
            holder.recyclerView.visibility = View.GONE
            holder.emptyText.text = "На ${dayConfig.dateText} пар нет"
        } else {
            holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.emptyText.visibility = View.GONE
            holder.recyclerView.visibility = View.VISIBLE
            holder.recyclerView.adapter = LessonsAdapter(lessonsForThisDay)
        }
    }
}