package com.interview.appscheduler.feature.scheduler.data.params

import com.interview.appscheduler.feature.scheduler.domain.params.GetAppListDomainParams

data class GetOutletCMRDataParams(
    val id: Int
)

fun GetAppListDomainParams.toDataParams(): GetOutletCMRDataParams {
    return GetOutletCMRDataParams(
        id = this.id
    )
}