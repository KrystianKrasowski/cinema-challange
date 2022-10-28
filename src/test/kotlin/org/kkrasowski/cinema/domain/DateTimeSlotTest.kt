package org.kkrasowski.cinema.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDateTime

class DateTimeSlotTest {

    @ParameterizedTest
    @CsvSource(
        "2022-10-21T08:00:00, 2022-10-21T11:00:00, false",
        "2022-10-21T09:00:00, 2022-10-21T11:59:59, false",
        "2022-10-21T09:00:00, 2022-10-21T12:00:00, true",
        "2022-10-21T10:00:00, 2022-10-21T13:00:00, true",
        "2022-10-21T11:00:00, 2022-10-21T14:00:00, true",
        "2022-10-21T12:00:00, 2022-10-21T15:00:00, true",
        "2022-10-21T13:00:00, 2022-10-21T16:00:00, true",
        "2022-10-21T14:00:00, 2022-10-21T17:00:00, true",
        "2022-10-21T15:00:00, 2022-10-21T17:00:00, true",
        "2022-10-21T15:00:01, 2022-10-21T17:00:00, false",
        "2022-10-21T16:00:00, 2022-10-21T19:00:00, false",
        "2022-10-21T17:00:00, 2022-10-21T20:00:00, false",
        "2022-10-21T18:00:00, 2022-10-21T21:00:00, false",
        "2022-10-21T19:00:00, 2022-10-21T22:00:00, false",
    )
    fun `should verify that slots clashes one with another`(startsAt: String, endsAt: String, clashes: Boolean) {
        // given
        val dateTimeSlotA = dateTimeSlotOf("A", "2022-10-21T12:00:00", "2022-10-21T15:00:00")
        val dateTimeSlotB = dateTimeSlotOf("B", startsAt, endsAt)

        // when
        val result1 = dateTimeSlotA.clashesWith(dateTimeSlotB)
        val result2 = dateTimeSlotB.clashesWith(dateTimeSlotA)

        // then
        assertThat(result1).isEqualTo(clashes)
        assertThat(result2).isEqualTo(clashes)
    }

    @ParameterizedTest
    @CsvSource(
        "2022-10-21T12:00:00, 2022-10-21T11:59:59",
        "2022-10-21T13:30:00, 2022-10-21T13:00:00",
        "2022-10-21T14:15:00, 2022-10-21T14:00:00",
        "2022-10-21T16:45:00, 2022-10-21T15:00:00",
    )
    fun `should not create date time slot`(startsAt: String, endsAt: String) {
        assertThatThrownBy { dateTimeSlotOf("Slot", LocalDateTime.parse(startsAt), LocalDateTime.parse(endsAt)) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
