package com.example.skga.presentation.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import domain.entity.DayConfig
import domain.entity.EventItem
import domain.entity.HomeListItem
import domain.entity.ScheduleItem

class ViewPagerAdapter(
    private val days: List<DayConfig>,
    private val allLessons: List<ScheduleItem>, // Передаем список всех уроков
    private val allEvents: List<EventItem>      // Передаем список всех событий
) : RecyclerView.Adapter<ViewPagerAdapter.DayViewHolder>() {

    override fun getItemCount(): Int = days.size

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSchedule)
        val emptyText: TextView = view.findViewById(R.id.emptyText)
        val dateTitle: TextView = view.findViewById(R.id.todayDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_day_item, parent, false)
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

        // 1. Фильтруем уроки для текущего дня (твоя логика + исправил пропущенную проверку подгруппы)
        val lessonsForThisDay = allLessons.filter { lesson ->
            val isCorrectDay = lesson.dayOfWeek == dayConfig.dayOfWeek
            val isCorrectWeek = lesson.weekType == 0 || lesson.weekType == dayConfig.currentWeekType

            val isCorrectSubgroup =
                lesson.subGroup == 0 || lesson.subGroup == dayConfig.currentWeekType

            isCorrectDay && isCorrectWeek && isCorrectSubgroup
        }

        // 2. Фильтруем события для текущего дня
        // ВНИМАНИЕ: Замени event.dateText на то поле, по которому в твоем EventItem хранится дата (например, "07.06.2026")
        val eventsForThisDay = allEvents.filter { event ->
            event.eventDate == dayConfig.dateIso
        }

        // 3. Объединяем оба списка в один список типа HomeListItem
        val combinedList = mutableListOf<HomeListItem>()

        // Превращаем ScheduleItem в HomeListItem.Lesson
        combinedList.addAll(lessonsForThisDay.map { HomeListItem.Lesson(it) })

        combinedList.addAll(eventsForThisDay.map { event ->
            HomeListItem.Event(
                EventItem(
                    eventDate = event.eventDate,
                    id = event.id,
                    eventDescription = event.eventDescription,
                    eventFaculties = event.eventFaculties,
                    eventGroups = event.eventGroups,
                    eventIsActual = event.eventIsActual,
                    eventIsGlobal = event.eventIsGlobal,
                    eventLocation = event.eventLocation,
                    eventName = event.eventName,
                    eventIsTeachers = event.eventIsTeachers,
                    eventTime = event.eventTime,
                    eventType = event.eventType,
                )
            )
        })

        val sortedCombinedList = combinedList.sortedBy { item ->
            when (item) {
                is HomeListItem.Lesson -> item.scheduleItem.lessonStartTime
                is HomeListItem.Event -> item.eventItem.eventTime
            }
        }

        // 5. Отображаем данные в RecyclerView
        if (sortedCombinedList.isEmpty()) {
            holder.emptyText.visibility = View.VISIBLE
            holder.recyclerView.visibility = View.GONE
            holder.emptyText.text = "На ${dayConfig.dateText} событий и пар нет"
        } else {
            holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.emptyText.visibility = View.GONE
            holder.recyclerView.visibility = View.VISIBLE

            holder.recyclerView.adapter = LessonsAdapter(sortedCombinedList)
        }
    }
}