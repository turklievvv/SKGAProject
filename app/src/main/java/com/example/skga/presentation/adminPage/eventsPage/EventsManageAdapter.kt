package com.example.skga.presentation.adminPage.eventsPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skga.R
import domain.entity.EventItem

class EventsManageAdapter(
    private val list: List<EventItem>
) : RecyclerView.Adapter<EventsManageAdapter.EventsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false)
        return EventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val item = list[position]
        val groupsText = item.eventGroups?.joinToString(", ") ?: ""
        val facultiesText = item.eventFaculties?.joinToString(", ") { facultyId ->
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
        if (item.eventIsTeachers) targetList.add("Для учителей")

        if (targetList.isNotEmpty()) {
            holder.eventPeoples.text = targetList.joinToString(", ")
        } else {
            holder.eventPeoples.text = "Для всех студентов"
        }

        holder.eventName.text = item.eventName
        holder.eventDate.text = item.eventDate
        holder.eventTime.text = item.eventTime
        holder.eventLocation.text = item.eventLocation
        holder.eventType.text = item.eventType
        holder.eventDescription.text = item.eventDescription
        if (item.eventIsGlobal) {
            holder.eventPeoples.text = "Для всех студентов"
        }
        if (item.eventIsActual) {
            holder.eventIsActual.visibility = View.GONE
        } else {
            holder.eventIsActual.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return list.size
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
}
