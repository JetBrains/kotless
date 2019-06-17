## WebApp Configuration

WebApp configuration in a Kotless gradle plugin defines project-specific 
deployment configuration.

First of all, `webapp` function receives `Project` as a first argument.
This project will be considered as a source of defined web application.
It means, that in sources of this project will be performed a lookup 
for DSL usage.

In WebApp configuration you must set `packages` variable -- it is a set
of packages under which Kotless will look for usages of DSL. All other
usages will be ignored.

You may set `route53` variable -- it defines a Route53 alias for your
application. You will need an ACM certificate in us-east-1 to create it.

It is possible to configure lambda parameters -- you should use `lambda`
function for it. You can set timeout of lambda, memory in MB and autowarm
configuration.

Here is the simple snippet of the webapp configuration:

```kotlin
webapp(project) {
    packages = setOf("org.example.kotless")
    route53 = Route53("kotless", "example.com")
    lambda {
        timeoutSec = 300
        memoryMb = 512
    }
}
```
