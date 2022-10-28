package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.Film
import org.kkrasowski.cinema.domain.FilmTitle

class FilmsCatalogueStub : FilmsCatalogue {

    private val films = mutableListOf<Film>()

    override fun find(title: FilmTitle): Film {
        return films.first { it.title == title }
    }

    fun containsFilm(film: Film) {
        films.add(film)
    }
}
