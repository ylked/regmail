package ch.hearc.nde.regmailapi.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
data class EmailVerificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(mappedBy = "emailVerification")
    val user: UserEntity? = null,

    @Column(unique = true)
    var token: String? = null,

    var shortCode: String? = null,

    var expiresAt: Long = 0,

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)