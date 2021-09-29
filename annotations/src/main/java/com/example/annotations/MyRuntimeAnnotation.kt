package com.example.annotations

import kotlin.reflect.KClass

@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
public annotation class MyRuntimeAnnotation(vararg val args: KClass<*>)