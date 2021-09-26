package com.example.b

import dagger.Binds
import dagger.Module

@Module
abstract class ModuleB {
    @Binds
    abstract fun bindB(b: ImplB): B
}