package org.kkrasowski.cinema.assertions

import org.assertj.core.api.AbstractAssert
import org.kkrasowski.cinema.domain.Failure
import org.kkrasowski.cinema.domain.RoomOccupation
import org.kkrasowski.cinema.domain.ScheduleResult
import org.kkrasowski.cinema.domain.ScheduleResult.Success
import org.kkrasowski.cinema.domain.ScheduledSeance
import org.kkrasowski.cinema.domain.ScheduleResult.Failure as ResultFailure

class ScheduleResultAssert(result: ScheduleResult) : AbstractAssert<ScheduleResultAssert, ScheduleResult>(result, ScheduleResultAssert::class.java) {

    fun isSuccess(): ScheduleSuccessAssert {
        if (actual !is Success) {
            failWithMessage("Expected result to be Success")
        }

        return ScheduleSuccessAssert(actual as Success)
    }

    fun isFailure() : ScheduleResultAssert {
        if (actual !is ResultFailure) {
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

class ScheduledSeanceAssert(result: ScheduledSeance?) : AbstractAssert<ScheduledSeanceAssert, ScheduledSeance>(result, ScheduledSeanceAssert::class.java) {

    fun hasVersion(version: Long): ScheduledSeanceAssert {
        isNotNull

        if (actual.version != version) {
            failWithMessage("Expected aggregate version to be <%s>, but was <%s>", version, actual.version)
        }

        return myself
    }

    fun contains(occupation: RoomOccupation): ScheduledSeanceAssert {
        isNotNull

        if (!actual.occupations.contains(occupation)) {
            failWithMessage("Expected scheduled occupations to contain <%s>", occupation)
        }

        return myself
    }
}

class FailureAssert(failure: Failure?) : AbstractAssert<FailureAssert, Failure>(failure, FailureAssert::class.java) {

    fun reasonIs(reason: String): FailureAssert {
        isNotNull

        if (actual.reason != reason) {
            failWithMessage("Expected reason to be <%s>, but was <%s>", reason, actual.reason)
        }

        return myself
    }
}


fun assertThat(result: ScheduleResult) = ScheduleResultAssert(result)

fun assertThat(result: ScheduledSeance?) = ScheduledSeanceAssert(result)

fun assertThat(failure: Failure) = FailureAssert(failure)
