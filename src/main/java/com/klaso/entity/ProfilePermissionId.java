package com.klaso.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class ProfilePermissionId implements java.io.Serializable {
    private static final long serialVersionUID = 4626012478785073354L;
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProfilePermissionId entity = (ProfilePermissionId) o;
        return Objects.equals(this.permissionId, entity.permissionId) &&
                Objects.equals(this.profileId, entity.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, profileId);
    }

}