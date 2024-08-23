package ch.hearc.nde.regmailapi

import ch.hearc.nde.regmailapi.service.AuthenticationService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RegmailApiApplication

fun main(args: Array<String>) {
    runApplication<RegmailApiApplication>(*args)
}
