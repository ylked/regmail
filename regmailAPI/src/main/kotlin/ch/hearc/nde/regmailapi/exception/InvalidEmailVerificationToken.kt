package ch.hearc.nde.regmailapi.exception

import java.lang.Exception

class InvalidEmailVerificationToken: Exception() {
    override val message: String
        get() = "Invalid email verification token"
}