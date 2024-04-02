package uk.co.goingproprogramming.tbp.services

import com.bugfender.sdk.Bugfender
import javax.inject.Inject

interface IServiceLogError {
    fun logError(throwable: Throwable)
}

class ServiceLogError @Inject constructor() : IServiceLogError {
    override fun logError(throwable: Throwable) {
        Bugfender.sendIssue("Error", throwable.stackTraceToString())
    }
}