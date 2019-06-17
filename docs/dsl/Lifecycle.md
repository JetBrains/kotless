## Lifecycle API

Kotless DSL provides an interface to control and extend lambda lifecycle.
It includes extension points for warming and initialization sequences.

### Warming

Kotless lambdas can be autowarmed. It means, that some scheduler will
periodically (by default, each 3 minutes) call lambda to be sure, that
it will not be displaced from hot pool of cloud provider.

Each call executes warming sequence. This sequence consists of all 
existing Kotlin static `object`-s implementing interface `LambdaWarming`.

Here is a simple snippet of HTTP connection warming:

```kotlin
object DbKeepAlive: LambdaWarming {
    override fun warmup() {
        Database.sendHeartBeat()
    }
}
```

Note: by default warming will trigger only initialization of Kotless
application, if it was not already initialized.

### Initialization

On first call (or during first warm-up) Kotless will perform initialization.

During initialization Kotless application scans code for Kotless resources
(like `LambdaWarming`, `ConversionService` and routes) and registers them.

After this Kotless will call `init` on existing Kotlin static `object`-s 
implementing interface `LambdaInit`. Call is guaranteed to happen only once.

You may use `LambdaInit` to prepare application before first execution. 


