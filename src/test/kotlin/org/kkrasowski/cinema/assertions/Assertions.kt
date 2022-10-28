package org.kkrasowski.cinema.assertions

import org.assertj.core.api.AbstractAssert
import org.kkrasowski.cinema.domain.RoomOccupation
import org.kkrasowski.cinema.domain.ScheduleResult
import org.kkrasowski.cinema.domain.ScheduleResult.Failure
import org.kkrasowski.cinema.domain.ScheduleResult.Success

class ScheduleResultAssert(result: ScheduleResult) : AbstractAssert<ScheduleResultAssert, ScheduleResult>(result, ScheduleResultAssert::class.java) {

    fun isSuccess(): ScheduleSuccessAssert {
        if (actual !is Success) {
            failWithMessage("Expected result to be Success")
        }

        return ScheduleSuccessAssert(actual as Success)
    }

    fun isFailure() : ScheduleResultAssert {
        if (actual !is Failure) {
            failWithMessage("Expected result to be Failure")
        }

        return myself
    }
}

class ScheduleSuccessAssert(result: Success) : AbstractAssert<ScheduleSuccessAssert, Success>(result, ScheduleSuccessAssert::class.java) {

    fun hasVersion(version: Long): ScheduleSuccessAssert {
        if (actual.version != version) {
            failWithMessage("Expected aggregate version to be <%s>, but was <%s>", version, actual.version)
        }

        return myself
    }

    fun contains(occupation: RoomOccupation): ScheduleSuccessAssert {
        if (!actual.occupations.contains(occupation)) {
            failWithMessage("Expected scheduled occupations to contain <%s>", occupation)
        }

        return myself
    }
}


fun assertThat(result: ScheduleResult) = ScheduleResultAssert(result)
