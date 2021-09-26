package com.example.a

import dagger.Binds
import dagger.Module

@Module
abstract class ModuleA {
    @Binds
    abstract fun bindA(a: ImplA): A
}