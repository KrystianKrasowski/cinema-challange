package org.kkrasowski.cinema.domain

import java.time.Duration
import java.time.temporal.TemporalAmount

data class Room(val name: RoomName, val maintenanceTime: Duration)

data class RoomName(val value: String)

data class RoomOccupation(val roomName: RoomName, val label: Label, private val slot: DateTimeSlot, private val attributes: Collection<Attribute>) {

    fun clashesWith(other: RoomOccupation): Boolean = slot.clashesWith(other.slot)

    fun clashesWith(others: Collection<RoomOccupation>): Boolean = others.any { it.clashesWith(this) }

    fun startsAfterOrAt(time: TemporalAmount): Boolean = slot.startsAfterOrExactlyAt(time)

    fun startsBefore(time: TemporalAmount): Boolean = slot.startsBefore(time)

    fun endsBeforeOrAt(time: TemporalAmount): Boolean = slot.endsBeforeOrExactlyAt(time)

    fun endsAfter(time: TemporalAmount): Boolean = slot.endsAfter(time)

    fun hasAttribute(attribute: Attribute): Boolean = attributes.contains(attribute)

    enum class Attribute {
        THE_3D_GLASSES_REQUIRED,
        PREMIERE
    }
}
