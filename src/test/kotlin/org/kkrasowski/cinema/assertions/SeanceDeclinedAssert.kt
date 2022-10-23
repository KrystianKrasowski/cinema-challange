package org.kkrasowski.cinema.assertions

import org.assertj.core.api.AbstractAssert
import org.kkrasowski.cinema.Seance

class SeanceDeclinedAssert(seance: Seance.Declined) : AbstractAssert<SeanceDeclinedAssert, Seance.Declined>(seance, SeanceDeclinedAssert::class.java) {
}
