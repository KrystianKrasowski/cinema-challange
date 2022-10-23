package org.kkrasowski.cinema.assertions

import org.kkrasowski.cinema.Seance

fun assertThat(seance: Seance) = SeanceAssert(seance)
