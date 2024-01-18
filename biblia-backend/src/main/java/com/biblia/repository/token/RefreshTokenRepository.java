package com.biblia.repository.token;

import com.biblia.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    RefreshToken deleteByToken(String token);

    @Modifying
    @Query(value = "DELETE FROM refresh_token WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId")Integer userId);
}
