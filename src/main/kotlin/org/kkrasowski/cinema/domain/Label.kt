package org.kkrasowski.cinema.domain

data class Label(val value: String)

fun labelOf(film: Film) = Label(film.title.value)

fun labelOfMaintenance() = Label("Maintenance")

// TODO: all extensions probably are needed only in test scope
fun String.toLabel() = Label(this)
