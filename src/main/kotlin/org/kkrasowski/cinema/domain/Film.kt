package org.kkrasowski.cinema.domain

import java.time.Duration

data class Film(val title: FilmTitle, val time: Duration, val type: Type) {

    enum class Type {
        REGULAR,
        PREMIERE
    }
}

data class FilmTitle(val value: String)

fun String.toFilmTitle() = FilmTitle(this)
