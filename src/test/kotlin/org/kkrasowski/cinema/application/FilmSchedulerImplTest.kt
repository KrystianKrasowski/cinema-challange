package org.kkrasowski.cinema.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.kkrasowski.cinema.api.*
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogueStub
import org.kkrasowski.cinema.spi.RoomsRepositoryStub
import org.kkrasowski.cinema.spi.ScheduleRepositoryStub
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

    @ParameterizedTest
    @ValueSource(strings = [
        "2022-10-21T08:00:01",
        "2022-10-21T09:00:00",
        "2022-10-21T10:00:00",
        "2022-10-21T11:00:00",
        "2022-10-21T12:00:00",
        "2022-10-21T13:00:00",
        "2022-10-21T13:30:00",
    ])
    fun `chosen room is unavailable at the given time`(startsAt: String) {
        // given
        filmCatalogue.containsFilm(filmOf("Cinderella", "PT2H"))
        roomsRepository.containsRoom(
            roomOf("Room 1", "PT1H", listOf(
            dateTimeSlotOf("Shrek", "2022-10-21T11:00:00", "2022-10-21T13:30:00"),
            dateTimeSlotOf("Maintenance", "2022-10-21T13:30:00", "2022-10-21T14:30:00"),
            dateTimeSlotOf("Unavailable", "2022-10-21T15:00:00", "2022-10-21T22:00:00")
        )))

        // when
        val seance = filmScheduler.schedule("Cinderella", "Room 1", startsAt)

        // then
        assertThat(seance).isEqualTo(Seance.DECLINED)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "2022-10-21T00:00:00",
        "2022-10-21T01:00:00",
        "2022-10-21T02:00:00",
        "2022-10-21T03:00:00",
        "2022-10-21T04:00:00",
        "2022-10-21T05:00:00",
        "2022-10-21T06:00:00",
        "2022-10-21T07:00:00",
        "2022-10-21T07:59:59",
        "2022-10-21T19:00:01",
        "2022-10-21T20:00:00",
        "2022-10-21T21:00:00",
        "2022-10-21T22:00:00",
        "2022-10-21T23:00:00",
        "2022-10-21T23:59:59",
    ])
    fun `the seance is out of cinema's working hours`(startsAt: String) {
        // given
        filmCatalogue.containsFilm(filmOf("Cinderella", "PT2H"))
        roomsRepository.containsRoom(roomOf("Room 1", "PT1H"))

        // when
        val seance = filmScheduler.schedule("Cinderella", "Room 1", startsAt)

        // then
        assertThat(seance).isEqualTo(Seance.DECLINED)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "2022-10-21T00:00:00",
        "2022-10-21T01:00:00",
        "2022-10-21T02:00:00",
        "2022-10-21T03:00:00",
        "2022-10-21T04:00:00",
        "2022-10-21T05:00:00",
        "2022-10-21T06:00:00",
        "2022-10-21T07:00:00",
        "2022-10-21T08:00:00",
        "2022-10-21T09:00:00",
        "2022-10-21T10:00:00",
        "2022-10-21T11:00:00",
        "2022-10-21T12:00:00",
        "2022-10-21T13:00:00",
        "2022-10-21T14:00:00",
        "2022-10-21T15:00:00",
        "2022-10-21T16:00:00",
        "2022-10-21T16:59:59",
        "2022-10-21T19:00:01",
        "2022-10-21T20:00:00",
        "2022-10-21T21:00:00",
        "2022-10-21T22:00:00",
        "2022-10-21T23:00:00",
        "2022-10-21T23:59:59",
    ])
    fun `the premiere seance is out of the premieres hours`(startsAt: String) {
        // given
        filmCatalogue.containsFilm(premiereFilmOf("Cinderella 3", "PT2H"))
        roomsRepository.containsRoom(roomOf("Room 1", "PT1H"))

        // when
        val seance = filmScheduler.schedule("Cinderella 3", "Room 1", startsAt)

        // then
        assertThat(seance).isEqualTo(Seance.DECLINED)
    }

    fun `the film requires 3D glasses`() {}

    fun `chosen film has not been found in the catalogue`() {}

    fun `chosen room does not appear in the repository`() {}

    private fun FilmScheduler.schedule(title: String, roomName: String, startsAt: String) = schedule(
        title = title.toFilmTitle(),
        roomName = roomName.toRoomName(),
        startsAt = LocalDateTime.parse(startsAt)
    )

}
