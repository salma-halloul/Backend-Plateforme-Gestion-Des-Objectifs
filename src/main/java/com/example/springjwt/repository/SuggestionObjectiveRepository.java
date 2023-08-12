package com.example.springjwt.repository;

import com.example.springjwt.models.SuggestionObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionObjectiveRepository extends JpaRepository<SuggestionObjective, Long> {
    List<SuggestionObjective> findBySuggestedById(Long userId);


}
