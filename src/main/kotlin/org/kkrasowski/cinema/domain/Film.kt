package org.kkrasowski.cinema.domain

import java.time.Duration

data class Film(val title: FilmTitle, val time: Duration, val type: Type, val display: Display) {

    enum class Type {
        REGULAR,
        PREMIERE
    }

    enum class Display {
        DISPLAY_2D,
        DISPLAY_3D
    }
}

data class FilmTitle(val value: String)

fun String.toFilmTitle() = FilmTitle(this)
