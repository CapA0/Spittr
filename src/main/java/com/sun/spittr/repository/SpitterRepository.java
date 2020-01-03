package com.sun.spittr.repository;

import com.sun.spittr.model.Spitter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpitterRepository extends JpaRepository<Spitter, Long> {
    Spitter findByUsername(String username);
}
