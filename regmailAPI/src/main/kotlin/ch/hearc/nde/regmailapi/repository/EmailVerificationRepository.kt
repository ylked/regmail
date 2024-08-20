package ch.hearc.nde.regmailapi.repository

import ch.hearc.nde.regmailapi.model.EmailVerificationEntity
import org.springframework.data.repository.CrudRepository

interface EmailVerificationRepository: CrudRepository<EmailVerificationEntity, Long> {
}