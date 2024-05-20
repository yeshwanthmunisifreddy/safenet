package com.thesubgraph.safenet.data.common

import android.content.Context

interface Mapper

interface ResponseDomainMapper<DomainModel> : Mapper {
    fun mapToDomain(): DomainModel?
}

interface ContextResponseDomainMapper<DomainModel> : Mapper {
    fun mapToDomain(context: Context): DomainModel?
}
