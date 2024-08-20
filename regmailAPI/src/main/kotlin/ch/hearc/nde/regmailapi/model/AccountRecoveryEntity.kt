package ch.hearc.nde.regmailapi.model

import jakarta.persistence.*

@Entity
data class AccountRecoveryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(mappedBy = "accountRecovery")
    var user: UserEntity? = null,

    @Column(unique = true)
    var token: String? = null,

    var shortCode: String? = null,

    var expiresAt: Long = 0,
)