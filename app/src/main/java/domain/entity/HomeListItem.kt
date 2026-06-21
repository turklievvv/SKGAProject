package domain.entity

sealed interface HomeListItem {

    data class Lesson (val scheduleItem: ScheduleItem) : HomeListItem

    data class Event(val eventItem: EventItem) : HomeListItem

}