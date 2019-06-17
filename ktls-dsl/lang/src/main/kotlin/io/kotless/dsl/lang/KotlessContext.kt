package io.kotless.dsl.lang

import io.kotless.dsl.events.HttpRequest

/**
 * Global context of Kotless execution
 *
 * Context is filled during dispatching of event and
 * will be automatically reset after it.
 */
object KotlessContext {
    object HTTP {
        private var currentRequest: ThreadLocal<HttpRequest?> = ThreadLocal.withInitial { null }

        internal fun reset() {
            currentRequest.remove()
        }

        /**
         * Current HTTP request, processed in a current thread.
         *
         * @throws IllegalStateException if this call is performed not in context of
         * HTTP request processing (basically, not in an HTTP route)
         */
        var request: HttpRequest
            get() = currentRequest.get() ?: throw IllegalStateException("Call outside HTTP request processing")
            internal set(value) {
                currentRequest.set(value)
            }
    }
}
