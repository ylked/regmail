package ch.hearc.nde.regmailapi.repository

import ch.hearc.nde.regmailapi.model.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {
}