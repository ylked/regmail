package ch.hearc.nde.regmailapi.exception

import java.lang.Exception

class InvalidEmailFormat: Exception() {
    override val message: String
        get() = "Invalid email format"
}