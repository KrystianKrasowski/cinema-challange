package org.kkrasowski.cinema.assertions

import org.assertj.core.api.AbstractAssert
import org.kkrasowski.cinema.Seance

class SeanceAssert(seance: Seance) : AbstractAssert<SeanceAssert, Seance>(seance, SeanceAssert::class.java) {

    fun isScheduled(): ScheduledSeanceAssert {
        if (actual !is Seance.Scheduled) {
            failWithMessage("Expected seance to be scheduled")
        }

        return ScheduledSeanceAssert(actual as Seance.Scheduled)
    }
}
