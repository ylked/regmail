package ch.hearc.nde.regmailapi.exception

class AlreadyVerified: Exception() {
    override val message: String = "Email already verified"
}