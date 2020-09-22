package com.martmists.hackgame.common

import java.lang.Exception

/**
 * Used to propagate disconnects
 */
class DisconnectException : Exception("Disconnect Triggered")