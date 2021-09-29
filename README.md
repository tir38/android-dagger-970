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
