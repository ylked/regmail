package ch.hearc.nde.regmailapi.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
data class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(
        name = "user_id",
    )
    val user: UserEntity? = null,

    val token: String = "",

    val expiresAt: Long = 0,

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
