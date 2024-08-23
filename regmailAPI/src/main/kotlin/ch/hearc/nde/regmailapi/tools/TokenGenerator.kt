package ch.hearc.nde.regmailapi.tools

class TokenGenerator {
    companion object {
        fun token(): String = java.util.UUID.randomUUID().toString()

        fun shortCode(): String {
            val chars = "0123456789"
            return (1..6)
                .map { chars.random() }
                .joinToString("")
        }
    }
}