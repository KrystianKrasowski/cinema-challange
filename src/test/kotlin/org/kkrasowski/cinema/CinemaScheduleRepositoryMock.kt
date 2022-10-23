package org.kkrasowski.cinema

class CinemaScheduleRepositoryMock : CinemaScheduleRepository {

    lateinit var cinemaSchedule: CinemaSchedule

    private val scheduledSeances = mutableListOf<Seance.Scheduled>()

    override fun fetch(): CinemaSchedule = cinemaSchedule

    override fun save(seance: Seance.Scheduled) {
        scheduledSeances.add(seance)
    }

    fun savedSeanceOnce(seance: Seance) {
        val seances = scheduledSeances.filter { it == seance }.size
        assert(seances == 1) {
            "Expected seance to be saved once, but saved $seances time(s)"
        }
    }
}
