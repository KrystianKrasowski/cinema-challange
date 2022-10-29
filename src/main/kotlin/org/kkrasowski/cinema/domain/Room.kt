package org.kkrasowski.cinema.domain

import arrow.core.left
import arrow.core.right
import java.time.Duration
import java.time.temporal.TemporalAmount

data class Room(val name: RoomName, val maintenanceTime: Duration)

data class RoomName(val value: String) {

    override fun toString(): String = value
}

data class RoomOccupation(val roomName: RoomName, val label: Label, private val slot: DateTimeSlot, private val attributes: Collection<Attribute>) {

    fun clashesWith(others: Collection<RoomOccupation>): Boolean = others.any { it.clashesWith(this) }

    fun startsBefore(time: TemporalAmount): Boolean = slot.startsBefore(time)

    fun endsAfter(time: TemporalAmount): Boolean = slot.endsAfter(time)

    fun hasAttribute(attribute: Attribute): Boolean = attributes.contains(attribute)

    private fun clashesWith(other: RoomOccupation): Boolean = slot.clashesWith(other.slot)

    enum class Attribute {
        THE_3D_GLASSES_REQUIRED,
        PREMIERE
    }
}

typealias RoomOccupations = Collection<RoomOccupation>
typealias Rooms = Collection<Room>

fun Rooms.getByName(name: RoomName) = firstOrNull { it.name == name }
    ?.right()
    ?: Failure("""There is no such room as "$name".""").left()
