package org.kkrasowski.cinema

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.kkrasowski.cinema.assertions.assertThat
import java.time.Duration
import java.time.LocalDateTime

class BasicSchedulerTest {

    private val room1: RoomConfigurer.() -> Unit = {
        name("Room 1")
        needs1HourOfMaintenanceTime()
    }

    private val cinemaScheduleRepository = CinemaScheduleRepositoryMock()

    @Test
    fun `the one where film is scheduled`() {
        // given
        val basicScheduler = BasicSchedulerConfigurer(cinemaScheduleRepository)
            .hasRoom(room1)
            .configure()

        // when
        val film = film2DOf("Cinderella", Duration.ofSeconds(9000))
        val room = RoomName("Room 1")
        val startTime = LocalDateTime.parse("2022-10-21T10:00:00")
        val seance = basicScheduler.schedule(film, room, startTime)

        // then
        assertThat(seance)
            .isScheduled()
            .filmTitleIs("Cinderella")
            .startsAt("2022-10-21 10:00")
            .endsAt("2022-10-21 12:30")
            .maintenanceStartsAt("2022-10-21 12:30")
            .maintenanceEndsAt("2022-10-21 13:30")

        // and
        cinemaScheduleRepository.savedSeanceOnce(seance)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "2022-10-21T13:00:00", // is it ok?
        "2022-10-21T14:00:00",
        "2022-10-21T15:00:00",
        "2022-10-21T16:00:00",
        "2022-10-21T17:00:00",
        "2022-10-21T18:00:00",
        "2022-10-21T18:59:59",
    ])
    fun `the one where room is occupied for given film`(startsAt: String) {
        // given
        val basicScheduler = BasicSchedulerConfigurer(cinemaScheduleRepository)
            .hasRoom(room1)
            .hasScheduledSeance {
                room(room1)
                film {
                    title("Shrek")
                    lastsForTwoHours()
                }
                startsAt("2022-10-21T16:00:00")
            }
            .configure()

        // when
        val film = film2DOf("Cinderella", Duration.ofMinutes(150))
        val room = RoomName("Room 1")
        val seance = basicScheduler.schedule(film, room, LocalDateTime.parse(startsAt))

        // then
        assertThat(seance).isDeclined()
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
    ])
    fun `the one where the basic seance is scheduled on invalid time`(startsAt: String) {
        // given
        val basicScheduler = BasicSchedulerConfigurer(cinemaScheduleRepository)
            .hasRoom {
                name("Room 1")
                needs1HourOfMaintenanceTime()
            }
            .opensAtOclock(8)
            .closesAtOclock(22)
            .configure()

        // when
        val film = film2DOf("Cinderella", Duration.ofHours(2))
        val room = RoomName("Room 1")
        val seance = basicScheduler.schedule(film, room, LocalDateTime.parse(startsAt))

        // then
        assertThat(seance).isDeclined()
    }

    // 17:00 - 20:00 from Monday to Sunday
    fun `the one where the premiere seance is scheduled on invalid time`() {

    }

    @Test
    fun `the one where 3D glasses are required by given the film`() {
        // given
        val basicScheduler = BasicSchedulerConfigurer(cinemaScheduleRepository)
            .hasRoom(room1)
            .configure()

        // when
        val film = film3DOf("Avatar", Duration.ofHours(3))
        val room = RoomName("Room 1")
        val seance = basicScheduler.schedule(film, room, LocalDateTime.parse("2022-10-21T12:00:00"))

        // then
        assertThat(seance)
            .isScheduled()
            .requires3DGlasses()
    }

    // Isn't it the same as occupation?
    fun `the one where room is unavailable for given time`() {

    }

    fun `the one where given room does not exist`() {

    }

    fun `the one where given start time is in the past`() {

    }
}

private class BasicSchedulerConfigurer(private val cinemaScheduleRepository: CinemaScheduleRepositoryMock) {

    private val rooms = mutableListOf<Room>()
    private val seances = mutableListOf<Seance.Scheduled>()
    private var openingHour = Duration.parse("PT1H")
    private var closingHour = Duration.parse("PT23H")

    fun hasRoom(roomConfigurer: RoomConfigurer.() -> Unit) = apply {
        rooms.add(RoomConfigurer().apply(roomConfigurer).build())
    }

    fun hasScheduledSeance(configurer: ScheduledSeanceConfigurer.() -> Unit) = apply {
        this.seances.add(ScheduledSeanceConfigurer().apply(configurer).build())
    }

    fun opensAtOclock(hour: Long) = apply { this.openingHour = Duration.ofHours(hour) }

    fun closesAtOclock(hour: Long) = apply { this.closingHour = Duration.ofHours(hour) }

    fun configure() = BasicScheduler(
        cinemaScheduleRepository.apply {
            cinemaSchedule = CinemaSchedule(
                rooms = Rooms(rooms.toList()),
                seances = ScheduledSeances(seances.toList()),
                openingHour = openingHour,
                closingHour = closingHour
            )
        }
    )
}

private class RoomConfigurer {

    private var name = ""
    private var maintenanceTime = Duration.ofHours(0)

    fun name(name: String) = apply { this.name = name }

    fun needs1HourOfMaintenanceTime() = apply { this.maintenanceTime = Duration.parse("PT1H") }

    fun build(): Room = Room(
        name = RoomName(name),
        maintenanceTime = maintenanceTime
    )
}

private class ScheduledSeanceConfigurer {

    private var room = RoomConfigurer().build()
    private var film = FilmConfigurer().build()
    private var startsAt = "1970-01-01T00:00:00"

    fun room(configurer: RoomConfigurer.() -> Unit) = apply { this.room = RoomConfigurer().apply(configurer).build() }

    fun film(configurer: FilmConfigurer.() -> Unit) = apply { this.film = FilmConfigurer().apply(configurer).build() }

    fun startsAt(startsAt: String) = apply { this.startsAt = startsAt }

    fun build() = Seance.Scheduled(
        room = room,
        film = film,
        startTime = LocalDateTime.parse(startsAt)
    )
}

private class FilmConfigurer {

    private var title = ""
    private var duration = Duration.ofHours(0)
    private var type = Film.DisplayType.DISPLAY_2D

    fun title(title: String) = apply { this.title = title }

    fun lastsForTwoHours() = apply { this.duration = Duration.ofHours(2) }

    fun build() = Film(
        title = title,
        duration = duration,
        displayType = type
    )
}

fun film2DOf(title: String, duration: Duration) = Film(title, duration, Film.DisplayType.DISPLAY_2D)

fun film3DOf(title: String, duration: Duration) = Film(title, duration, Film.DisplayType.DISPLAY_3D)
