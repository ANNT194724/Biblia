package com.biblia.repository.token;

import com.biblia.entity.User;
import com.biblia.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    VerificationToken findByUser(User user);

    @Transactional
    @Modifying
    @Query("UPDATE VerificationToken c SET c.verifiedTime = ?2 WHERE c.user.userId = ?1")
    int updateConfirmedAt(Integer userId, Timestamp confirmedAt);
}
