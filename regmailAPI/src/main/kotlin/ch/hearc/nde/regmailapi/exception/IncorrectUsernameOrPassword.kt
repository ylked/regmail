package ch.hearc.nde.regmailapi.exception

class IncorrectUsernameOrPassword: Exception() {
    override val message: String
        get() = "Incorrect username or password"
}