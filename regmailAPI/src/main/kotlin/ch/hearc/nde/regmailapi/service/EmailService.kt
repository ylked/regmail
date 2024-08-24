package ch.hearc.nde.regmailapi.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService @Autowired constructor(
    private val emailSender: JavaMailSender
){

    fun sendEmail(to: String?, subject: String?, body: String?) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.text = body

        emailSender.send(message)
    }
}