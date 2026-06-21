package com.example.skga.presentation.schedulePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import com.example.skga.presentation.homePage.LessonsAdapter
import com.example.skga.presentation.homePage.ViewPagerAdapter
import domain.entity.DayConfig
import domain.entity.HomeListItem
import domain.entity.ScheduleItem

class DayAdapter(private val items: List<HomeListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LESSON = 0
        private const val TYPE_EVENT = 1
    }

    class LessonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val queue: TextView = view.findViewById(R.id.queueNumber)
        val tvEndTime: TextView = view.findViewById(R.id.lessonEndTime)
        val tvStartTime: TextView = view.findViewById(R.id.lessonStartTime)
        val lessonName: TextView = view.findViewById(R.id.lessonName)
        val tvTeacher: TextView = view.findViewById(R.id.teacherName)
        val tvRoom: TextView = view.findViewById(R.id.lessonRoom)
    }

    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventNameTV)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
        val eventPeoples: TextView = itemView.findViewById(R.id.eventPeoples)
        val eventType: TextView = itemView.findViewById(R.id.eventType)
        val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        val eventIsActual: ImageView = itemView.findViewById(R.id.isActualIc)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeListItem.Lesson -> TYPE_LESSON
            is HomeListItem.Event -> TYPE_EVENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LESSON -> {
                val view = inflater.inflate(R.layout.lesson_item, parent, false)
                LessonViewHolder(view)
            }

            TYPE_EVENT -> {
                val view = inflater.inflate(R.layout.event_item, parent, false)
                EventsViewHolder(view)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LessonViewHolder -> {
                val lesson = (items[position] as HomeListItem.Lesson).scheduleItem

                holder.queue.text = lesson.lessonNumber.toString()
                holder.tvStartTime.text = lesson.lessonStartTime
                holder.tvEndTime.text = lesson.lessonEndTime
                holder.lessonName.text = lesson.lessonName
                holder.tvTeacher.text = lesson.lessonTeacherShortName
                holder.tvRoom.text = "Ауд: ${lesson.lessonClassRoom}"
            }

            is EventsViewHolder -> {
                val event = (items[position] as HomeListItem.Event).eventItem
                val groupsText = event.eventGroups?.joinToString(", ") ?: ""
                val facultiesText = event.eventFaculties?.joinToString(", ") { facultyId ->
                    when (facultyId) {
                        1 -> "ИЭ"
                        2 -> "ИЦТ"
                        3 -> "ИИ"
                        else -> "Факультет $facultyId"
                    }
                } ?: ""

                val targetList = mutableListOf<String>()
                if (groupsText.isNotEmpty()) targetList.add(groupsText)
                if (facultiesText.isNotEmpty()) targetList.add(facultiesText)
                if (event.eventIsTeachers) targetList.add("Для учителей")

                if (targetList.isNotEmpty()) {
                    holder.eventPeoples.text = targetList.joinToString(", ")
                } else {
                    holder.eventPeoples.text = "Для всех студентов"
                }

                holder.eventName.text = event.eventName
                holder.eventDate.text = event.eventDate
                holder.eventTime.text = event.eventTime
                holder.eventLocation.text = event.eventLocation
                holder.eventType.text = event.eventType
                holder.eventDescription.text = event.eventDescription
                if (event.eventIsGlobal) {
                    holder.eventPeoples.text = "Для всех студентов"
                }
                if (event.eventIsActual) {
                    holder.eventIsActual.visibility = View.GONE
                } else {
                    holder.eventIsActual.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}