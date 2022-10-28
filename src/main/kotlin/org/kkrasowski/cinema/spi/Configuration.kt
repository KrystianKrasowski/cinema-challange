package org.kkrasowski.cinema.spi

import java.time.temporal.TemporalAmount

interface Configuration {

    val openingHour: TemporalAmount
    val closingHour: TemporalAmount
    val premieresStartHour: TemporalAmount
    val premieresEndHour: TemporalAmount
}
