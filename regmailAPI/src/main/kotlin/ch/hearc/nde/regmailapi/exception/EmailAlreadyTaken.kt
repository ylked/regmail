package ch.hearc.nde.regmailapi.exception

import java.lang.Exception

class EmailAlreadyTaken: Exception() {
    override val message: String
        get() = "Email already taken"
}