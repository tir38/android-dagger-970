# Trying to reproduce issue #970

For some reason I'm unable to reproduce issue [#970](https://github.com/google/dagger/issues/970).


`app module` <---- `module b` <---- `module a`


Using Dagger 2.38.1

If I build component tree in Application/Activity, obviously `app` needs to depend on `module-a`, either directly or transitively via `module-b`: (`api project(path: ':a')`

```
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
		   ...
        val componentA = DaggerComponentA.factory()
            .create()
        val componentB = DaggerComponentB.factory()
            .create(componentA)
        val appThing = DaggerAppComponent
            .factory()
            .create(componentB)
            .appThing()
    }
}
```

But if I contain component creation to each Gradle module by using a `Holder`_++_ object, then I don't need to expose `module-a` to `app module`. :celebrate:

```
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

	// All A-creation is encapsulated in B
    object Holder {
        val componentB = DaggerComponentB.factory()
            .create(ComponentA.Holder.componentA) 
            
    }
}
```

Application/Activity becomes:


```
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ...

        // try to use class A here. Ya can't!!
        
        val appThing = AppComponent.Holder.appComponent
            .appThing()
    }
}
```

If dagger is validating the whole graph this shouldn't work.


_++I hate this name. I don't know what to call it_



------

### A note about exposing types through constructor and annotation.

[@oehme points out](https://github.com/google/dagger/issues/970#issuecomment-551917809) that the "middle" `componentB` exposes a type `B` which has a constructor argument on `A` and `componentB` also has an annotation dependency on `componentA`

I think the later can be solved by having componentB only expose the interface. i.e. B should be an interface.

But that doesn't prevent the former. But that had me thinking, Is an argument *within* an annotation part of a class/interface's public API. I create a stone-simple example based on the same `app` <-- `b` <--- `a` dependency structure: https://github.com/tir38/android-dagger-970/tree/annotation-only

```
package com.example.b

import com.example.a.ExceptionFromA

/**
 * Is ExceptionFromA part of the public API of this interface?
 */
interface AnnotatedB {
  @Throws(
    exceptionClasses = [ExceptionFromA::class]
  )
  fun throws()
}
```

Within the app module I can create an implementation of AnnotatedB without needing to `api project(path: ':a')`

```
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    MyThing().throws()
  }

  class MyThing : AnnotatedB {
    override fun throws() {
      // did I pick up the annotation here?
    }
  }
}
```