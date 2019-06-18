# Changelog
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

# 1.0.0 — 2019-06-18
### Added
* Explicitly declared permissions, e.g. `@S3Bucket(bucket = "my_bucket", mode = Mode.Read`
    * Works for functions, classes and objects
    * Taken from routes and global actions (like `LambdaWarming`, `LambdaInit` and so on)
* `LambdaWarming` sequences - functions to execute each warming cycle
* `LambdaInit` sequences - functions to execute on initialization of lambda
* `HttpRequestInterceptor` - interceptors for HTTP requests, maybe chained
* Possibility to extend serialization and deserialization
* Links built-in support -- base links and links with parameters
