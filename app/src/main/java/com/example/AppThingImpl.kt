package com.example

import com.example.b.B
import javax.inject.Inject

class AppThingImpl @Inject constructor(val b: B) : AppThing