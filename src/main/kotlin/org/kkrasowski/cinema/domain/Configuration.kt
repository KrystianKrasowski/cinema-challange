package org.kkrasowski.cinema.domain

import java.time.temporal.TemporalAmount

data class Configuration(val openingHour: TemporalAmount,
                         val closingHour: TemporalAmount,
                         val premieresStartHour: TemporalAmount,
                         val premieresEndHour: TemporalAmount)
