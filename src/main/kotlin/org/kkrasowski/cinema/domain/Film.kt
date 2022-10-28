package org.kkrasowski.cinema.domain

import java.time.Duration

data class Film(val title: FilmTitle, val time: Duration)
data class FilmTitle(val value: String)
