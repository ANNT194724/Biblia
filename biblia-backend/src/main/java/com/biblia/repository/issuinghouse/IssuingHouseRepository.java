package com.biblia.repository.issuinghouse;

import com.biblia.entity.IssuingHouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuingHouseRepository extends JpaRepository<IssuingHouse, Integer> {
    List<IssuingHouse> findByNameContainsAndDeleteFlagOrderByName(String name, Integer deleteFlag);

    Page<IssuingHouse> findByNameContainsAndDeleteFlagOrderByName(String name, Integer deleteFlag, Pageable pageable);

    IssuingHouse findByIssuingHouseIdAndDeleteFlag(Integer issuingHouseId, Integer deleteFlag);
}
