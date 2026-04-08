package com.klaso.dto;

import com.klaso.entity.Establishment;
import java.time.Instant;

public class EstablishmentDto {
    public Long id;
    public String name;
    public String address;
    public String city;
    public String postalCode;
    public String country;
    public String periodType;
    public String academicYear;
    public Long ownerId;
    public String ownerName;
    public Instant createdAt;
    public Instant updatedAt;

    public EstablishmentDto() {}

    public EstablishmentDto(Establishment entity) {
        if (entity == null) return;
        this.id = entity.getId();
        this.name = entity.getName();
        this.address = entity.getAddress();
        this.city = entity.getCity();
        this.postalCode = entity.getPostalCode();
        this.country = entity.getCountry();
        this.periodType = entity.getPeriodType();
        this.academicYear = entity.getAcademicYear();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        
        if (entity.getOwner() != null) {
            this.ownerId = entity.getOwner().getId();
            if (entity.getOwner().getUser() != null) {
                String firstName = entity.getOwner().getUser().getFirstName() != null ? entity.getOwner().getUser().getFirstName() : "";
                String lastName = entity.getOwner().getUser().getLastName() != null ? entity.getOwner().getUser().getLastName() : "";
                this.ownerName = (firstName + " " + lastName).trim();
            }
        }
    }
}
