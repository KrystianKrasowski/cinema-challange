package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.Film
import org.kkrasowski.cinema.domain.FilmTitle

interface FilmsCatalogue {

    fun find(title: FilmTitle): Film
}
