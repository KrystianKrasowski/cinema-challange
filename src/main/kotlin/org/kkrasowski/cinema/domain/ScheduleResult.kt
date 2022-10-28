package org.kkrasowski.cinema.domain

sealed class ScheduleResult {

    fun onSuccess(block: (Success) -> Unit) = apply {
        if (this@ScheduleResult is Success) {
            block(this@ScheduleResult)
        }
    }

    data class Success(val version: Long, val occupations: Collection<RoomOccupation>) : ScheduleResult()

    object Failure : ScheduleResult()
}
