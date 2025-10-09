package com.interview.appscheduler.core.abstraction

interface AbstractUseCase<out ReturnType, in Params> {
    suspend operator fun invoke(): ReturnType
    suspend operator fun invoke(params: Params): ReturnType
}