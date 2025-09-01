package com.klaso.service;

import com.klaso.dto.UserLoginDTO;
import com.klaso.dto.UserRegisterDTO;
import com.klaso.entity.User;
import com.klaso.repository.UserRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;


import java.time.Instant;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public User register(UserRegisterDTO dto) {
        User user = new User();
        user.setEmail(dto.email);
        user.setFirstName(dto.firstName);
        user.setLastName(dto.lastName);
        user.setPassword(BCrypt.hashpw(dto.password, BCrypt.gensalt()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.persist(user);
        return user;
    }

    public String login(UserLoginDTO dto) {
        User user = userRepository.findByEmail(dto.email);
        if (user == null || !BCrypt.checkpw(dto.password, user.getPassword())) {
            throw new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED);
        }
        return Jwt.issuer("klaso")
                .upn(user.getEmail())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getFirstName() + " " + user.getLastName())
                .sign();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}