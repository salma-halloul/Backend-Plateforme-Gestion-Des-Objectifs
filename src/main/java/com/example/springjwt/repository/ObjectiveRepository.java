package com.example.springjwt.repository;

import com.example.springjwt.models.Objective;
import com.example.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    List<Objective> findByOwner(User owner);

}
