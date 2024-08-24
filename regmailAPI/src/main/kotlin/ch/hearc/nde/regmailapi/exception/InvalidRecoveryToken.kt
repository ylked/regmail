package ch.hearc.nde.regmailapi.exception

class InvalidRecoveryToken: Exception(){
    override val message: String = "Invalid recovery token"
}