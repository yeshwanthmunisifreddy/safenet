package com.thesubgraph.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class RepositoryModule(
    val type: KClass<*>,
)