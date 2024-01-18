package com.biblia.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "verification_token", schema = "biblia")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer tokenId;
    @Basic
    @Column(name = "token")
    private String token;
    @Basic
    @Column(name = "created_time")
    private Timestamp createdTime;
    @Basic
    @Column(name = "expired_time")
    private Timestamp expiredTime;
    @Basic
    @Column(name = "verified_time")
    private Timestamp verifiedTime;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public VerificationToken(String token, LocalDateTime now, LocalDateTime plusMinutes, User user) {
        this.token = token;
        this.createdTime = Timestamp.valueOf(now);
        this.expiredTime = Timestamp.valueOf(plusMinutes);
        this.user = user;
    }
}
