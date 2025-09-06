package com.klaso.repository;

import com.klaso.entity.User;
import io.quarkiverse.groovy.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public User findByResetToken(String token) {
        return find("resetToken", token).firstResult();
    }
}