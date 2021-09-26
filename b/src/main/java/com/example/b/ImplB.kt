package com.example.b

import com.example.a.A
import javax.inject.Inject

class ImplB @Inject constructor(val a: A) : B