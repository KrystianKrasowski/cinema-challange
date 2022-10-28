package org.kkrasowski.cinema.domain

sealed class Seance {
    object Scheduled : Seance()
    object Declined : Seance()
}
