package com.biblia.repository.publisher;

import com.biblia.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    List<Publisher> findByNameContainsAndDeleteFlagOrderByName(String name, Integer deleteFlag);

    Page<Publisher> findByNameContainsAndDeleteFlagOrderByName(String name, Integer deleteFlag, Pageable pageable);

    Publisher findByPublisherIdAndDeleteFlag(Integer publisherId, Integer deleteFlag);
}
