package com.biblia.repository.language;

import com.biblia.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
    List<Language> findLanguageByNameContainsOrLocalContains(String name, String local);
}
