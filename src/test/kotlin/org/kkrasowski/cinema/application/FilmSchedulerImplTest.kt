package org.kkrasowski.cinema.application

import arrow.core.Either
import arrow.core.Validated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.kkrasowski.cinema.api.FilmScheduler
import org.kkrasowski.cinema.assertions.assertThat
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.domain.RoomOccupation.Attribute
import org.kkrasowski.cinema.spi.FilmsCatalogueStub
import org.kkrasowski.cinema.spi.ScheduleRepositoryStub
import java.time.Duration
import java.time.LocalDateTime

class FilmSchedulerImplTest {

    private val filmCatalogue = FilmsCatalogueStub()
    private val scheduleRepository = ScheduleRepositoryStub()

    private val filmScheduler
        get() = FilmSchedulerImpl(filmCatalogue, scheduleRepository)

    @BeforeEach
    fun setUp() {
        scheduleRepository
            .openingHourIs(Duration.ofHours(8))
            .closingHourIs(Duration.ofHours(22))
            .premieresStartAt(Duration.ofHours(17))
            .premieresEndAt(Duration.ofHours(21))
    }

    @Test
    fun `seance is scheduled`() {
        // given
        filmCatalogue.containsFilm(filmOf("Cinderella", "PT2H"))
        scheduleRepository
            .hasVersion(1)
            .hasDefinedRoom(roomOf("Room 1", "PT1H"))

        // when
        val result = filmScheduler.schedule("Cinderella", "Room 1", "2022-10-21T09:00:00")

        // then
        assertThat(result)
            .isSuccess()
            .hasVersion(2)
            .contains(roomOccupationOf("Room 1", "Cinderella", dateTimeSlotOf("2022-10-21T09:00:00", "2022-10-21T11:00:00")))
            .contains(roomOccupationOf("Room 1", "Maintenance", dateTimeSlotOf("2022-10-21T11:00:00", "2022-10-21T12:00:00")))

        assertThat(scheduleRepository.getOccupations())
            .containsAll(result.asSuccess().occupations)
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
        "2022-10-21T14:45:00",
    ])
    fun `chosen room is unavailable at the given time`(startsAt: String) {
        // given
        filmCatalogue.containsFilm(filmOf("Cinderella", "PT2H"))
        scheduleRepository
            .hasDefinedRoom(roomOf("Room 1", "PT1H"))
            .containsOccupation(roomOccupationOf("Room 1", "Shrek 1", dateTimeSlotOf("2022-10-21T11:00:00", "2022-10-21T13:30:00")))
            .containsOccupation(roomOccupationOf("Room 1", "Maintenance", dateTimeSlotOf("2022-10-21T13:30:00", "2022-10-21T14:30:00")))
            .containsOccupation(roomOccupationOf("Room 1", "Out of order", dateTimeSlotOf("2022-10-21T15:00:00", "2022-10-21T22:00:00")))

        // when
        val result = filmScheduler.schedule("Cinderella", "Room 1", startsAt)

        // then
        assertThat(result).isFailure()
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
        scheduleRepository.hasDefinedRoom(roomOf("Room 1", "PT1H"))

        // when
        val result = filmScheduler.schedule("Cinderella", "Room 1", startsAt)

        // then
        assertThat(result).isFailure()
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
        scheduleRepository.hasDefinedRoom(roomOf("Room 1", "PT1H"))

        // when
        val result = filmScheduler.schedule("Cinderella 3", "Room 1", startsAt)

        // then
        assertThat(result).isFailure()
    }

    @Test
    fun `the film requires 3D glasses`() {
        // given
        filmCatalogue.containsFilm(film3DOf("Avatar", "PT2H"))
        scheduleRepository.hasDefinedRoom(roomOf("Room 1", "PT1H"))

        // when
        filmScheduler.schedule("Avatar", "Room 1", "2022-10-21T09:00:00")

        // then
        assertThat(scheduleRepository.getOccupations())
            .contains(roomOccupationOf("Room 1", "Avatar", dateTimeSlotOf("2022-10-21T09:00:00", "2022-10-21T11:00:00"), listOf(
                Attribute.THE_3D_GLASSES_REQUIRED
            )))
    }

    @Test
    fun `schedule seance save fails`() {
        // given
        filmCatalogue.containsFilm(film3DOf("Avatar", "PT2H"))
        scheduleRepository
            .hasDefinedRoom(roomOf("Room 1", "PT1H"))
            .saveFailsDueTo("Saving failure")

        // when
        val result = filmScheduler.schedule("Avatar", "Room 1", "2022-10-21T09:00:00")

        // then
        assertThat(result).isFailure()
    }

    fun `chosen film has not been found in the catalogue`() {}

    fun `chosen room does not appear in the repository`() {}

    private fun FilmScheduler.schedule(title: String, roomName: String, startsAt: String): ScheduleResult {
        return when (val schedule = schedule(title.toFilmTitle(), roomName.toRoomName(), LocalDateTime.parse(startsAt))) {
            is Either.Right -> ScheduleResult.Success(schedule.value.version, schedule.value.occupations)
            is Either.Left -> ScheduleResult.Failure(schedule.value.reason)
        }
    }
}
