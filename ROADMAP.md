# Kotless roadmap

## 0.1.*
* Support of dynamic entrypoints in Ktor
    * Support new mode -- map everything using catch-all path variables
* Support of Spring Boot
* Extension for Cognito authentication
* Support of Azure
* DSL libraries for S3, DynamoDB, SSM at least
* Event handlers - functions as handlers for different AWS events

## 0.2.*
* IDEA plugin
    * Inspections on permissions granting - detect usage of AWS SDK functions that are
      not permitted explicitly
    * Deployed functions logs - possibility to attach IDEA console to CloudWatch log of
      specific lambda

## To discuss
* Implicit permissions flow - possibility to deduce permissions from AWS SDK functions
  only.

## Later plans
* Async calls of other lambdas via `async { ... }`. Other lambda will be 
  generated from body of `async` function.
* Async batch calls of other lambdas via `asyncBatching(list, max = N) { ... }`.
 Will use FireHose to batch passed elements from different calls of lambda into
 N elements packs and pass them to body for batch execution.
* Implementation of Kotlin/Native and Kotlin/JS dispatchers
