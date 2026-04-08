package com.klaso.service;

import com.klaso.entity.Account;
import com.klaso.entity.Establishment;
import com.klaso.repository.AccountRepository;
import com.klaso.repository.EstablishmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class EstablishmentService {

    @Inject
    EstablishmentRepository establishmentRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    JsonWebToken jwt;

    private Account getCurrentAccount() {
        String username = jwt.getName();
        if (username == null) return null;
        return accountRepository.findByUsername(username);
    }

    private boolean isAdmin() {
        return jwt.getGroups().contains("ADMIN");
    }

    public List<Establishment> getAll() {
        if (isAdmin()) {
            return establishmentRepository.listAll();
        }
        Account current = getCurrentAccount();
        if (current == null) return List.of();
        return establishmentRepository.findByOwner(current.getId());
    }

    @Transactional
    public Establishment create(Establishment establishment) {
        Account current = getCurrentAccount();
        if (current == null) {
            throw new ForbiddenException("Vous devez être connecté pour créer un établissement.");
        }
        
        establishment.setOwner(current);
        establishment.setCreatedAt(Instant.now());
        establishment.setUpdatedAt(Instant.now());
        establishmentRepository.persist(establishment);
        
        // Charger explicitement les infos pour éviter la LazyInitializationException
        if (establishment.getOwner() != null && establishment.getOwner().getUser() != null) {
            establishment.getOwner().getUser().getFirstName();
        }
        
        return establishment;
    }

    @Transactional
    public Establishment update(Long id, Establishment data) {
        Establishment existing = findById(id);
        if (existing == null) return null;

        existing.setName(data.getName());
        existing.setAddress(data.getAddress());
        existing.setCity(data.getCity());
        existing.setPostalCode(data.getPostalCode());
        existing.setCountry(data.getCountry());
        existing.setPeriodType(data.getPeriodType());
        existing.setAcademicYear(data.getAcademicYear());
        existing.setUpdatedAt(Instant.now());

        // Charger explicitement les infos pour éviter la LazyInitializationException
        if (existing.getOwner() != null && existing.getOwner().getUser() != null) {
            existing.getOwner().getUser().getFirstName();
        }

        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        Establishment existing = findById(id);
        if (existing == null) return false;
        return establishmentRepository.deleteById(id);
    }

    public Establishment findById(Long id) {
        Establishment establishment = establishmentRepository.findById(id);
        if (establishment == null) return null;

        // Si c'est un admin, il a accès à tout
        if (isAdmin()) return establishment;

        // Sinon, on vérifie que c'est bien le propriétaire
        Account current = getCurrentAccount();
        if (current == null || !establishment.getOwner().getId().equals(current.getId())) {
            throw new ForbiddenException("Vous n'avez pas accès à cet établissement.");
        }

        // Charger explicitement les infos pour éviter la LazyInitializationException
        if (establishment.getOwner() != null && establishment.getOwner().getUser() != null) {
            establishment.getOwner().getUser().getFirstName();
        }

        return establishment;
    }
}