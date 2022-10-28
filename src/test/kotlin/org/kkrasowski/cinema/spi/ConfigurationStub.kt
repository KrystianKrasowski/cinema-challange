package org.kkrasowski.cinema.spi

import java.time.temporal.TemporalAmount

class ConfigurationStub(override val openingHour: TemporalAmount,
                        override val closingHour: TemporalAmount,
                        override val premieresStartHour: TemporalAmount,
                        override val premieresEndHour: TemporalAmount) : Configuration
