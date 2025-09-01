package com.klaso.dto;

import com.klaso.entity.Classroom;
import com.klaso.entity.Establishment;

import java.time.Instant;

public class ClassroomResponseDto {

    public Long id;
    public String name;
    public String level;
    public String subject;
    public String academicYear;
    public Long establishmentId;
    public String establishmentName;
    public Instant createdAt;
    public Instant updatedAt;

    public static ClassroomResponseDto fromEntity(Classroom classroom) {
        ClassroomResponseDto dto = new ClassroomResponseDto();
        dto.id = classroom.getId();
        dto.name = classroom.getName();
        dto.level = classroom.getLevel();
        dto.subject = classroom.getSubject();
        dto.academicYear = classroom.getAcademicYear();
        dto.createdAt = classroom.getCreatedAt();
        dto.updatedAt = classroom.getUpdatedAt();

        Establishment est = classroom.getEstablishment();
        if (est != null) {
            dto.establishmentId = est.getId();
            dto.establishmentName = est.getName(); // ou autre champ représentatif
        }
        return dto;
    }
}