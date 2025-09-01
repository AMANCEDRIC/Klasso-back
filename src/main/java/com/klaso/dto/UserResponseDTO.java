package com.klaso.dto;

import com.klaso.entity.User;

public class UserResponseDTO {

    public Long id;
    public String email;
    public String firstName;
    public String lastName;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}