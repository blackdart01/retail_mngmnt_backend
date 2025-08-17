package com.retail.management.service;

import com.retail.management.entity.Expenditure;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ExpenditureService {
    Expenditure create(Expenditure expenditure);
    Expenditure update(Long id, Expenditure expenditure);
    Optional<Expenditure> getById(Long id);
    List<Expenditure> listAll();
    void delete(Long id);
    List<Expenditure> getExpendituresBetween(Instant from, Instant to);
}
