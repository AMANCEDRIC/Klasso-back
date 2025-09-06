package com.klaso.service;

import com.klaso.dto.UserLoginDTO;
import com.klaso.dto.UserRegisterDTO;
import com.klaso.entity.User;
import com.klaso.repository.UserRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    Mailer mailer;

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

    @Transactional
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // Pour des raisons de sécurité, on ne révèle pas si l'email existe ou non
            return;
        }

        // Générer un token unique
        String resetToken = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(3600); // Token valide 1 heure

        // Sauvegarder le token dans la base de données
        user.setResetToken(resetToken);
        user.setResetTokenExpiresAt(expiresAt);
        user.setUpdatedAt(Instant.now());
        userRepository.persist(user);

        // Envoyer l'email
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
        String emailBody = String.format(
            "Bonjour %s,\n\n" +
            "Vous avez demandé une réinitialisation de votre mot de passe.\n\n" +
            "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" +
            "%s\n\n" +
            "Ce lien est valide pendant 1 heure.\n\n" +
            "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\n" +
            "Cordialement,\n" +
            "L'équipe Klaso",
            user.getFirstName() != null ? user.getFirstName() : "Utilisateur",
            resetLink
        );

        mailer.send(Mail.withText(email, "Réinitialisation de votre mot de passe - Klaso", emailBody));
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        
        if (user == null || user.getResetTokenExpiresAt() == null || 
            user.getResetTokenExpiresAt().isBefore(Instant.now())) {
            throw new WebApplicationException("Token invalide ou expiré", Response.Status.BAD_REQUEST);
        }

        // Mettre à jour le mot de passe
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        user.setUpdatedAt(Instant.now());
        userRepository.persist(user);
    }
}