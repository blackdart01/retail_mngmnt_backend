package com.retail.management.repository;

import com.retail.management.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {
    List<Expenditure> findByDateBetween(Instant from, Instant to);
}
