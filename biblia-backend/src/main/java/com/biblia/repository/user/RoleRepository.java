package com.biblia.repository.user;

import com.biblia.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleCodeAndDeleteFlag(String roleCode, Integer deleteFlg);
}
