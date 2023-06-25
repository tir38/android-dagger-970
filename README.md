# Trying to reproduce issue #970

_See update at bottom for "fix"_

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



## Update

For whatever reason above, I did not know about `validateTransitiveComponentDependencies` nor did I set to `DISABLED`. So the project was behaving as if the check was disables, even though it defaults to `enabled` :shrug:

I start by confirm that everything still as I left it:

```
$ ./gradlew app:assembleDebug -Dorg.gradle.java.home=/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home/
```

Yep, still builds without exposing `api project(path: ':a')` and without `validateTransitiveComponentDependencies = Disabled`

Now bump everything:

1. switch to Gradle's JavaToolchain to set Java version to 17 "everywhere"
2. Bump to lastest KGP: 1.9.0-RC
3. Bump to latest AGP 8.1.0-rc01
4. Bump to latest Dagger 2.46.1
5. Bump to latest Gradle 8.0

build:

```
$ ./gradlew app:assembleDebug -Dorg.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home/

```

Issue resurfaced (with a much nicer error message)


```
> Task :app:kaptDebugKotlin FAILED
error: ComponentProcessingStep was unable to process 'com.example.AppComponent' because 'com.example.a.ComponentA' could not be resolved.

  Dependency trace:
      => element (INTERFACE): com.example.b.ComponentB
      => annotation: @dagger.Component(modules={com.example.b.ModuleB}, dependencies={com.example.a.ComponentA})
      => annotation value (TYPE_ARRAY): dependencies={com.example.a.ComponentA}
      => annotation value (TYPE): dependencies=com.example.a.ComponentA

  If type 'com.example.a.ComponentA' is a generated type, check above for compilation errors that may have prevented the type from being generated. Otherwise, ensure that type 'com.example.a.ComponentA' is on your classpath.
```  
  
So now I finally need to add `validateTransitiveComponentDependencies = Disabled` to `app/build.gradle` and the app once again builds fine without having to expose `api project(path: ':a')`.