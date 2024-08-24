package ch.hearc.nde.regmailapi.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
data class AccountRecoveryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(mappedBy = "accountRecovery")
    var user: UserEntity? = null,

    @Column(unique = true)
    var token: String? = null,

    var expiresAt: Long = 0,

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)