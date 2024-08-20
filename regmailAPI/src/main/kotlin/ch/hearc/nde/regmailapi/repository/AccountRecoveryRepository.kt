package ch.hearc.nde.regmailapi.repository

import ch.hearc.nde.regmailapi.model.AccountRecoveryEntity
import org.springframework.data.repository.CrudRepository

interface AccountRecoveryRepository: CrudRepository<AccountRecoveryEntity, Long> {
}