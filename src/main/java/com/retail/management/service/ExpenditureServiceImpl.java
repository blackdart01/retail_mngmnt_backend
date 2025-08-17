package com.retail.management.service;

import com.retail.management.entity.Expenditure;
import com.retail.management.repository.ExpenditureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenditureServiceImpl implements ExpenditureService {

    private final ExpenditureRepository expenditureRepository;

    public ExpenditureServiceImpl(ExpenditureRepository expenditureRepository) {
        this.expenditureRepository = expenditureRepository;
    }

    @Override
    public Expenditure create(Expenditure expenditure) {
        expenditure.setDate(Instant.now());
        return expenditureRepository.save(expenditure);
    }

    @Override
    public Expenditure update(Long id, Expenditure expenditure) {
        Expenditure existing = expenditureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expenditure not found"));
        existing.setDescription(expenditure.getDescription());
        existing.setAmount(expenditure.getAmount());
        existing.setCategory(expenditure.getCategory());
        return expenditureRepository.save(existing);
    }

    @Override
    public Optional<Expenditure> getById(Long id) {
        return expenditureRepository.findById(id);
    }

    @Override
    public List<Expenditure> listAll() {
        return expenditureRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        expenditureRepository.deleteById(id);
    }

    @Override
    public List<Expenditure> getExpendituresBetween(Instant from, Instant to) {
        return expenditureRepository.findByDateBetween(from, to);
    }
}
