package ch.hearc.nde.regmailapi.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    var email: String = "",

    var _password: String = "",
    var verified: Boolean = false,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(
        name = "email_verification_id",
        unique = true,
        nullable = true
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

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
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