package ch.hearc.nde.regmailapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService @Autowired constructor(
    private val emailSender: JavaMailSender
){

    private val logger: Logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendEmail(to: String?, subject: String?, body: String?) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.text = body

        emailSender.send(message)
        logger.info("Email sent to $to")
    }
}