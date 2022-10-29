package org.kkrasowski.cinema.assertions

import org.assertj.core.api.AbstractAssert
import org.kkrasowski.cinema.application.ScheduleResult
import org.kkrasowski.cinema.application.ScheduleResult.Failure
import org.kkrasowski.cinema.application.ScheduleResult.Success
import org.kkrasowski.cinema.domain.RoomOccupation

class ScheduleResultAssert(result: ScheduleResult) : AbstractAssert<ScheduleResultAssert, ScheduleResult>(result, ScheduleResultAssert::class.java) {

    fun isSuccess(): ScheduleSuccessAssert {
        if (actual !is Success) {
            failWithMessage("Expected result to be Success, but was <%s>", actual)
        }

        return ScheduleSuccessAssert(actual as Success)
    }

    fun isFailure() : ScheduleFailureAssert {
        if (actual !is Failure) {
            failWithMessage("Expected result to be Failure")
        }

        return ScheduleFailureAssert(actual as Failure)
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

class ScheduleFailureAssert(result: Failure) : AbstractAssert<ScheduleFailureAssert, Failure>(result, ScheduleFailureAssert::class.java) {

    fun hasReason(reason: String): ScheduleFailureAssert {
        if (actual.reason != reason) {
            failWithMessage("Expected reason to be <%s>, but was <%s>", reason, actual.reason)
        }

        return myself
    }
}

fun assertThat(result: ScheduleResult) = ScheduleResultAssert(result)
