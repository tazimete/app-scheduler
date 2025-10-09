package com.interview.appscheduler.feature.scheduler.domain.entity

import com.google.gson.annotations.SerializedName

data class AppEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("appId")
    val appId: Int,
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("packageName")
    val packageName: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("isScheduled")
    val isScheduled: Boolean,
    @SerializedName("scheduledTime")
    val scheduledTime: String,
    @SerializedName("installedTime")
    val installedTime: String
)