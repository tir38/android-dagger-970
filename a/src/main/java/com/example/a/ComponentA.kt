package com.example.a

import dagger.Component

@Component(
    modules = [ModuleA::class]
)
interface ComponentA {
    fun a(): A

    @Component.Factory
    interface Factory {
        fun create(): ComponentA
    }

    object Holder {
        val componentA = DaggerComponentA.factory()
            .create()
    }
}