package com.klaso.repository;

import com.klaso.entity.Permission;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PermissionRepository implements PanacheRepository<Permission> {
}
