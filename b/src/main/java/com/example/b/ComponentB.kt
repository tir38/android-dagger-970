package com.example.b

import com.example.a.ComponentA
import dagger.Component

@Component(
    modules = [ModuleB::class],
    dependencies = [ComponentA::class]
)
interface ComponentB {
    fun b(): B

    @Component.Factory
    interface Factory {
        fun create(componentA: ComponentA): ComponentB
    }

    object Holder {
        val componentB = DaggerComponentB.factory()
            .create(ComponentA.Holder.componentA)
    }
}