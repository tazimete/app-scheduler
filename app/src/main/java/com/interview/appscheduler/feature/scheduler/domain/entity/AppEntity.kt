package com.interview.appscheduler.feature.scheduler.domain.entity

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

data class AppEntity(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("appId")
    val appId: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("packageName")
    val packageName: String? = null,
    val icon: Drawable? = null,
    val versionCode: Long? = null,
    val versionName: String? = null,
    val isSystemApp: Boolean? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("isScheduled")
    val isScheduled: Boolean? = false,
    @SerializedName("scheduledTime")
    val scheduledTime: String? = null,
    @SerializedName("installedTime")
    val installedTime: String? = null
)