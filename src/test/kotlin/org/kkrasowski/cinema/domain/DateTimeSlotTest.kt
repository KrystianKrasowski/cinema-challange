package org.kkrasowski.cinema.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Duration
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
        val dateTimeSlotA = dateTimeSlotOf("2022-10-21T12:00:00", "2022-10-21T15:00:00")
        val dateTimeSlotB = dateTimeSlotOf(startsAt, endsAt)

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
        assertThatThrownBy { dateTimeSlotOf(LocalDateTime.parse(startsAt), LocalDateTime.parse(endsAt)) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @CsvSource(
        "PT0H, true",
        "PT1H, true",
        "PT2H, true",
        "PT3H, true",
        "PT4H, true",
        "PT5H, true",
        "PT6H, true",
        "PT7H, true",
        "PT8H, true",
        "PT9H, true",
        "PT10H, true",
        "PT11H, true",
        "PT12H, true",
        "PT12H1S, false",
        "PT13H, false",
        "PT14H, false",
        "PT15H, false",
        "PT16H, false",
        "PT17H, false",
        "PT18H, false",
        "PT19H, false",
        "PT20H, false",
        "PT21H, false",
        "PT22H, false",
        "PT23H, false",
    )
    fun `should verify that slot starts after or exactly at given time`(time: String, startsAfter: Boolean) {
        // given
        val slot = dateTimeSlotOf("2022-10-21T12:00:00", "2022-10-21T14:00:00")

        // when
        val result = slot.startsAfterOrExactlyAt(Duration.parse(time))

        // then
        assertThat(result).isEqualTo(startsAfter)
    }

    @ParameterizedTest
    @CsvSource(
        "PT0H, false",
        "PT1H, false",
        "PT2H, false",
        "PT3H, false",
        "PT4H, false",
        "PT5H, false",
        "PT6H, false",
        "PT7H, false",
        "PT8H, false",
        "PT9H, false",
        "PT10H, false",
        "PT11H, false",
        "PT12H, false",
        "PT13H, false",
        "PT14H, true",
        "PT15H, true",
        "PT16H, true",
        "PT17H, true",
        "PT18H, true",
        "PT19H, true",
        "PT20H, true",
        "PT21H, true",
        "PT22H, true",
        "PT23H, true",
    )
    fun `should verify that slot ends before or exactly at given time`(time: String, endsAt: Boolean) {
        // given
        val slot = dateTimeSlotOf("2022-10-21T12:00:00", "2022-10-21T14:00:00")

        // when
        val result = slot.endsBeforeOrExactlyAt(Duration.parse(time))

        // then
        assertThat(result).isEqualTo(endsAt)
    }
}
