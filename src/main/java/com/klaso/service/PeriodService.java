package com.klaso.service;

import com.klaso.entity.Establishment;
import com.klaso.entity.Period;
import com.klaso.repository.PeriodRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class PeriodService {

    @Inject
    PeriodRepository periodRepository;

    @Inject
    EstablishmentService establishmentService;

    public List<Period> findByEstablishmentId(Long establishmentId) {
        // Sécurisé par establishmentService (vérifie la propriété)
        establishmentService.findById(establishmentId);
        return periodRepository.list("establishment.id = ?1", establishmentId);
    }

    public Period findById(Long id) {
        Period period = periodRepository.findById(id);
        if (period == null) {
            throw new NotFoundException("Période introuvable");
        }
        // Sécurisé par establishmentService via l'établissement parent
        establishmentService.findById(period.getEstablishment().getId());
        return period;
    }

    @Transactional
    public void createDefaultPeriods(Establishment establishment) {
        String type = establishment.getPeriodType(); // "TRIMESTRE" | "SEMESTRE"
        int count = "SEMESTRE".equalsIgnoreCase(type) ? 2 : 3;
        String prefix = "SEMESTRE".equalsIgnoreCase(type) ? "Semestre " : "Trimestre ";

        for (int i = 1; i <= count; i++) {
            Period period = new Period();
            period.setEstablishment(establishment);
            period.setName(prefix + i);
            period.setType(type);
            period.setNumber(i);
            period.setAcademicYear(establishment.getAcademicYear());
            period.setCreatedAt(Instant.now());
            period.setUpdatedAt(Instant.now());
            periodRepository.persist(period);
        }
    }

    @Transactional
    public Period create(Period period, Long establishmentId) {
        Establishment establishment = establishmentService.findById(establishmentId);
        period.setEstablishment(establishment);
        period.setCreatedAt(Instant.now());
        period.setUpdatedAt(Instant.now());
        periodRepository.persist(period);
        return period;
    }

    @Transactional
    public Period update(Long id, Period period) {
        Period existing = findById(id);
        existing.setName(period.getName());
        existing.setStartDate(period.getStartDate());
        existing.setEndDate(period.getEndDate());
        existing.setAcademicYear(period.getAcademicYear());
        existing.setUpdatedAt(Instant.now());
        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        Period period = findById(id);
        periodRepository.delete(period);
        return true;
    }
}
