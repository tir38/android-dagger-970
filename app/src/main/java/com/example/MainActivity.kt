package com.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.b.AnnotatedB

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // try to use class A here. Ya can't!!

    val appThing = AppComponent.Holder.appComponent
      .appThing()

    val myBy = MyB()

    val thing = MyB::getObjects
  }

  class MyB : AnnotatedB {

  }
}