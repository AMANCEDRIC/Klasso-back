package com.klaso.repository;

import com.klaso.entity.Account;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account> {
    public Account findByUsername(String username) {
        return find("username", username).firstResult();
    }
}
