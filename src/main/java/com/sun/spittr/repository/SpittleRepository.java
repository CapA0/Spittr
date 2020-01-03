package com.sun.spittr.repository;

import com.sun.spittr.model.Spitter;
import com.sun.spittr.model.Spittle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SpittleRepository extends JpaRepository<Spittle, Long>, PagingAndSortingRepository<Spittle, Long> {
    public List<Spittle> findBySpitter(Spitter spitter);

}
