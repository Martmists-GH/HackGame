package com.martmists.common.utilities

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface Loggable {
    private val logger: Logger
        get() = LoggerFactory.getLogger(this::class.java)

    fun info(message: String) = logger.info("$message${TextColor.RESET}")
    fun info(message: String, vararg args: Any) = logger.info("$message${TextColor.RESET}", args)
    fun info(message: String, throwable: Throwable) = logger.info("$message${TextColor.RESET}", throwable)
    fun info(message: String, throwable: Throwable, vararg args: Any) = logger.info("$message${TextColor.RESET}", throwable, args)

    fun warn(message: String) = logger.warn("$message${TextColor.RESET}")
    fun warn(message: String, vararg args: Any) = logger.warn("$message${TextColor.RESET}", args)
    fun warn(message: String, throwable: Throwable) = logger.warn("$message${TextColor.RESET}", throwable)
    fun warn(message: String, throwable: Throwable, vararg args: Any) = logger.warn("$message${TextColor.RESET}", throwable, args)

    fun error(message: String) = logger.error("$message${TextColor.RESET}")
    fun error(message: String, vararg args: Any) = logger.error("$message${TextColor.RESET}", args)
    fun error(message: String, throwable: Throwable) = logger.error("$message${TextColor.RESET}", throwable)
    fun error(message: String, throwable: Throwable, vararg args: Any) = logger.error("$message${TextColor.RESET}", throwable, args)

    fun debug(message: String) = logger.debug("$message${TextColor.RESET}")
    fun debug(message: String, vararg args: Any) = logger.debug("$message${TextColor.RESET}", args)
    fun debug(message: String, throwable: Throwable) = logger.debug("$message${TextColor.RESET}", throwable)
    fun debug(message: String, throwable: Throwable, vararg args: Any) = logger.debug("$message${TextColor.RESET}", throwable, args)

    fun trace(message: String) = logger.trace("$message${TextColor.RESET}")
    fun trace(message: String, vararg args: Any) = logger.trace("$message${TextColor.RESET}", args)
    fun trace(message: String, throwable: Throwable) = logger.trace("$message${TextColor.RESET}", throwable)
    fun trace(message: String, throwable: Throwable, vararg args: Any) = logger.trace("$message${TextColor.RESET}", throwable, args)
}
