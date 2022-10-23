package org.kkrasowski.cinema.assertions

import org.assertj.core.api.AbstractAssert
import org.kkrasowski.cinema.Seance

class SeanceAssert(seance: Seance) : AbstractAssert<SeanceAssert, Seance>(seance, SeanceAssert::class.java) {

    fun isScheduled(): SeanceScheduledAssert {
        if (actual !is Seance.Scheduled) {
            failWithMessage("Expected seance to be scheduled")
        }

        return SeanceScheduledAssert(actual as Seance.Scheduled)
    }

    fun isDeclined(): SeanceDeclinedAssert {
        if (actual !is Seance.Declined) {
            failWithMessage("Expected seance to be declined")
        }

        return SeanceDeclinedAssert(actual as Seance.Declined)
    }
}
