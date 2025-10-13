package com.interview.appscheduler.feature.scheduler.domain.entity

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AppEntity(
    @SerializedName("id")
    var id: Long? = null,
    @SerializedName("appId")
    val appId: Int? = null,
    @SerializedName("taskId")
    var taskId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("packageName")
    val packageName: String,
    val icon: Drawable? = null,
    val versionCode: Long? = null,
    val versionName: String? = null,
    val isSystemApp: Boolean? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("isScheduled")
    val isScheduled: Boolean,
    @SerializedName("scheduledTime")
    var scheduledTime: String? = null,
    @SerializedName("installedTime")
    val installedTime: String? = null,
    @SerializedName("status")
    val status: Int? = null
)