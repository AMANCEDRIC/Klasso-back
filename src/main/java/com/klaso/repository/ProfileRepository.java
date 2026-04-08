package com.klaso.repository;

import com.klaso.entity.Profile;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class ProfileRepository implements PanacheRepository<Profile> {
    public Profile findByCode(String code) {
        return find("code", code).firstResult();
    }
}
