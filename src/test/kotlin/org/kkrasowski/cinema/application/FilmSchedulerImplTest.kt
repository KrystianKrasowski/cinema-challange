package org.kkrasowski.cinema.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kkrasowski.cinema.api.*
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogueStub
import org.kkrasowski.cinema.spi.RoomsRepositoryStub
import org.kkrasowski.cinema.spi.ScheduleRepositoryStub
import java.time.Duration
import java.time.LocalDateTime

class FilmSchedulerImplTest {

    private val filmCatalogue = FilmsCatalogueStub()
    private val scheduleRepository = ScheduleRepositoryStub()
    private val roomsRepository = RoomsRepositoryStub()

    private val filmScheduler
        get() = FilmSchedulerImpl(filmCatalogue, scheduleRepository, roomsRepository)

    @Test
    fun `seance is scheduled`() {
        // given
        filmCatalogue.containsFilm(filmOf("Cinderella", "PT2H"))
        roomsRepository.containsRoom(roomOf("Room 1", "PT1H"))

        // when
        val seance = filmScheduler.schedule("Cinderella", "Room 1", "2022-10-21T09:00:00")

        // then
        //TODO: Consider returning sealed class subtype with scheduled slots (maybe other test)
        assertThat(seance).isEqualTo(Seance.SCHEDULED)
        assertThat(scheduleRepository.getSlotsForRoom("Room 1"))
            .contains(dateTimeSlotOf("Cinderella", "2022-10-21T09:00:00", "2022-10-21T11:00:00"))
            .contains(dateTimeSlotOf("Maintenance", "2022-10-21T11:00:00", "2022-10-21T12:00:00"))
    }

    fun `chosen room is unavailable at the given time`() {}

    fun `the seance is out of cinema's working hours`() {}

    fun `the premiere seance is out of the premieres hours`() {}

    fun `the film requires 3D glasses`() {}

    fun `the seance clashes with another one`() {}

    fun `chosen film has not been found in the catalogue`() {}

    fun `chosen room does not appear in the repository`() {}

    private fun FilmScheduler.schedule(title: String, roomName: String, startsAt: String) = schedule(
        title = FilmTitle(title),
        roomName = RoomName(roomName),
        startsAt = LocalDateTime.parse(startsAt)
    )

}

// TODO: Extract these functions somewhere
private fun filmOf(title: String, duration: String) = Film(
    title = FilmTitle(title),
    time = Duration.parse(duration)
)

private fun roomOf(name: String, maintenanceTime: String) = Room(
    name = RoomName(name),
    maintenanceTime = Duration.parse(maintenanceTime)
)

private fun dateTimeSlotOf(name: String, start: String, end: String) = DateTimeSlot(
    name = name,
    start = LocalDateTime.parse(start),
    end = LocalDateTime.parse(end)
)
