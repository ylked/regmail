package ch.hearc.nde.regmailapi.exception

import java.lang.Exception

class TooManyRequests: Exception() {
    override val message: String
        get() = "Too many requests. Please wait a moment before trying again."
}