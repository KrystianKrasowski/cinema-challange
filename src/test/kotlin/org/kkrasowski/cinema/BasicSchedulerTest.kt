package org.kkrasowski.cinema

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kkrasowski.cinema.assertions.assertThat
import java.time.Duration
import java.time.LocalDateTime

class BasicSchedulerTest {

    private val cinemaScheduleRepository = CinemaScheduleRepositoryMock()
    private lateinit var basicScheduler: BasicScheduler

    @BeforeEach
    fun setUp() {
        basicScheduler = BasicSchedulerConfigurer(cinemaScheduleRepository)
            .room {
                hasName("Room 1")
                needs1HourOfMaintenanceTime()
            }
            .configure()
    }

    @Test
    fun `should schedule basic seance`() {
        // when
        val film = Film("Cinderella", Duration.ofSeconds(9000))
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
}

private class BasicSchedulerConfigurer(private val cinemaScheduleRepository: CinemaScheduleRepositoryMock) {

    private val rooms = mutableListOf<Room>()

    fun room(roomConfigurer: RoomConfigurer.() -> Unit): BasicSchedulerConfigurer = apply {
        rooms.add(RoomConfigurer().apply(roomConfigurer).build())
    }

    fun configure() = BasicScheduler(
        cinemaScheduleRepository.apply {
            cinemaSchedule = CinemaSchedule(
                rooms = Rooms(rooms.toList())
            )
        }
    )
}

private class RoomConfigurer {

    private var name = ""
    private var maintenanceTime = Duration.ofHours(0)

    fun hasName(name: String) = apply { this.name = name }

    fun needs1HourOfMaintenanceTime() = apply { this.maintenanceTime = Duration.parse("PT1H") }

    fun build(): Room = Room(
        name = RoomName(name),
        maintenanceTime = maintenanceTime
    )
}
