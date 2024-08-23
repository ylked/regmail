package ch.hearc.nde.regmailapi.repository

import ch.hearc.nde.regmailapi.model.RefreshTokenEntity
import ch.hearc.nde.regmailapi.model.UserEntity
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshTokenEntity, Long> {
    fun deleteByUser(user: UserEntity)
    fun findByToken(token: String): RefreshTokenEntity?
}