package ch.hearc.nde.regmailapi.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class Test {

    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello World")
    }
}