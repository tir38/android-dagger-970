package com.example

import com.example.b.ComponentB
import dagger.Component

@Component(
    modules = [AppModule::class],
    dependencies = [ComponentB::class]
)
interface AppComponent {
    fun appThing(): AppThing

    @Component.Factory
    interface Factory {
        fun create(componentB: ComponentB): AppComponent
    }

    object Holder {
        val appComponent = DaggerAppComponent.factory()
            .create(ComponentB.Holder.componentB)
    }
}