package com.example.b

import com.example.a.ComponentA
import dagger.Component

@Component(
    modules = [ModuleB::class],
    dependencies = [ComponentA::class]
)
interface ComponentB


