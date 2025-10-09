package com.interview.appscheduler.core.domain

import com.google.gson.annotations.SerializedName

class Entity<T>(
    @SerializedName("isSuccess")
    var isSuccess: Boolean = true,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var data: T? = null,
)