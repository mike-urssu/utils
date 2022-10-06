package logging

import mu.KotlinLogging

class LoggingForKotlin {
    private val log = KotlinLogging.logger {}

    fun printLogs() {
        log.trace { "trace" }
        log.debug { "debug" }
        log.info { "info" }
        log.warn { "warn" }
        log.error { "error" }
    }
}

fun main() {
    LoggingForKotlin().printLogs()
}