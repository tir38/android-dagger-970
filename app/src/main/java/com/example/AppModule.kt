package com.example

import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {
    @Binds
    abstract fun bindsAppThing(appThing: AppThingImpl): AppThing
}