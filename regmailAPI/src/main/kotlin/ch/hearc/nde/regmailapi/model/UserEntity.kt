package ch.hearc.nde.regmailapi.model

import jakarta.persistence.*

@Entity
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val username: String = "",

    @Column(unique = true)
    val email: String = "",

    val password: String = "",

    @OneToOne()
    @JoinColumn(
        name = "email_verification_id",
        unique = true,
        nullable = false
    )
    val emailVerification: EmailVerificationEntity? = null,

    @OneToOne()
    @JoinColumn(
        name = "account_recovery_id",
        unique = true,
        nullable = true
    )
    val accountRecovery: AccountRecoveryEntity? = null,

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val refreshToken: List<RefreshTokenEntity?> = emptyList(),
)