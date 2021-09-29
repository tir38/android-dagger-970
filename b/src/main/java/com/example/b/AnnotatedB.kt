package com.example.b

import com.example.a.A
import com.example.annotations.MyRuntimeAnnotation

/**
 * Is "A" part of the API for this interface?
 */
@MyRuntimeAnnotation(
  args = [A::class]
)
interface AnnotatedB