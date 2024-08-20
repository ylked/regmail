package ch.hearc.nde.regmailapi.repository

import ch.hearc.nde.regmailapi.model.RefreshTokenEntity
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshTokenEntity, Long> {
}