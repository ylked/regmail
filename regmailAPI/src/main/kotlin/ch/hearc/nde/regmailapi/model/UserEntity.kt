package ch.hearc.nde.regmailapi.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val email: String = "",

    val _password: String = "",

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(
        name = "email_verification_id",
        unique = true,
        nullable = false
    )
    var emailVerification: EmailVerificationEntity? = null,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(
        name = "account_recovery_id",
        unique = true,
        nullable = true
    )
    var accountRecovery: AccountRecoveryEntity? = null,

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var refreshToken: MutableList<RefreshTokenEntity?> = mutableListOf(),
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getPassword(): String {
        return this._password
    }

    override fun getUsername(): String {
        return this.email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}