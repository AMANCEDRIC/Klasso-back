package com.klaso.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profile_permission")
public class ProfilePermission {
    @EmbeddedId
    private ProfilePermissionId id;

    @MapsId("profileId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @MapsId("permissionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    public ProfilePermissionId getId() {
        return id;
    }

    public void setId(ProfilePermissionId id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

}