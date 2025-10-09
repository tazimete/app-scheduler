package com.interview.appscheduler.core.data

import com.google.gson.annotations.SerializedName

class Response<T> (
    @SerializedName("isSuccess")
    var isSuccess: Boolean = true,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var data: T? = null,
)