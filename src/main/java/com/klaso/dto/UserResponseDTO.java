package com.klaso.dto;

import com.klaso.entity.Account;
import com.klaso.entity.User;

public class UserResponseDTO {

    public Long id;
    public String email;
    public String firstName;
    public String lastName;
    public String role;
    public Long accountId;
    public boolean isBlocked;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public UserResponseDTO(User user, String role) {
        this(user);
        this.role = role;
    }

    public UserResponseDTO(Account account) {
        this(account.getUser());
        this.accountId = account.getId();
        this.isBlocked = !Boolean.TRUE.equals(account.getIsActive());
        if (account.getProfile() != null) {
            this.role = account.getProfile().getCode();
        }
    }
}