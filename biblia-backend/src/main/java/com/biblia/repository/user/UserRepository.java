package com.biblia.repository.user;

import com.biblia.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLoginIdAndDeleteFlag(String loginId, Integer deleteFlag);

    Optional<User> findByLoginIdAndStatusAndDeleteFlag(String loginId, Integer status, Integer deleteFlag);

    Optional<User> findByUserIdAndDeleteFlag(Integer userId, Integer deleteFlag);

    Page<User> findByLoginIdContainsOrUsernameContainsAndDeleteFlag
            (String loginId, String username, Integer deleteFlg, Pageable pageable);

    Boolean existsByLoginIdAndStatusAndDeleteFlag(String loginId, Integer status, Integer deleteFlag);


}
