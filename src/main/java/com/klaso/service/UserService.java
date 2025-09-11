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
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
        String userName = user.getFirstName() != null ? user.getFirstName() : "Utilisateur";
        
        // Version texte pour les clients qui ne supportent pas HTML
        String textBody = String.format(
            "Bonjour %s,\n\n" +
            "Vous avez demandé une réinitialisation de votre mot de passe.\n\n" +
            "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" +
            "%s\n\n" +
            "Ce lien est valide pendant 1 heure.\n\n" +
            "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\n" +
            "Cordialement,\n" +
            "L'équipe Klaso",
            userName,
            resetLink
        );
        
        // Version HTML avec design moderne
        String htmlBody = String.format(
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "    <meta charset=\"UTF-8\">" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "    <title>Réinitialisation de mot de passe</title>" +
            "    <style>" +
            "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
            "        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; }" +
            "        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; }" +
            "        .header h1 { margin: 0; font-size: 28px; font-weight: 300; }" +
            "        .content { padding: 40px 30px; }" +
            "        .greeting { font-size: 18px; color: #333; margin-bottom: 20px; }" +
            "        .message { font-size: 16px; color: #666; line-height: 1.6; margin-bottom: 30px; }" +
            "        .button-container { text-align: center; margin: 30px 0; }" +
            "        .reset-button { " +
            "            display: inline-block; " +
            "            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); " +
            "            color: white; " +
            "            text-decoration: none; " +
            "            padding: 15px 30px; " +
            "            border-radius: 25px; " +
            "            font-size: 16px; " +
            "            font-weight: 600; " +
            "            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3); " +
            "            transition: transform 0.2s ease;" +
            "        }" +
            "        .reset-button:hover { transform: translateY(-2px); }" +
            "        .warning { background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 8px; padding: 15px; margin: 20px 0; }" +
            "        .warning-text { color: #856404; font-size: 14px; margin: 0; }" +
            "        .footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 14px; }" +
            "        .link-fallback { margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 8px; word-break: break-all; }" +
            "        .link-fallback a { color: #667eea; text-decoration: none; }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "    <div class=\"container\">" +
            "        <div class=\"header\">" +
            "            <h1>🔐 Klaso</h1>" +
            "        </div>" +
            "        <div class=\"content\">" +
            "            <div class=\"greeting\">Bonjour %s,</div>" +
            "            <div class=\"message\">" +
            "                Vous avez demandé une réinitialisation de votre mot de passe. " +
            "                Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe sécurisé." +
            "            </div>" +
            "            <div class=\"button-container\">" +
            "                <a href=\"%s\" class=\"reset-button\">Réinitialiser mon mot de passe</a>" +
            "            </div>" +
            "            <div class=\"warning\">" +
            "                <p class=\"warning-text\">" +
            "                    ⚠️ Ce lien est valide pendant <strong>1 heure</strong> seulement. " +
            "                    Si vous n'avez pas demandé cette réinitialisation, ignorez cet email." +
            "                </p>" +
            "            </div>" +
            "            <div class=\"link-fallback\">" +
            "                <p style=\"margin: 0 0 10px 0; font-size: 14px; color: #666;\">" +
            "                    Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :" +
            "                </p>" +
            "                <a href=\"%s\">%s</a>" +
            "            </div>" +
            "        </div>" +
            "        <div class=\"footer\">" +
            "            <p>Cordialement,<br><strong>L'équipe Klaso</strong></p>" +
            "            <p style=\"font-size: 12px; color: #999; margin-top: 20px;\">" +
            "                Cet email a été envoyé automatiquement, merci de ne pas y répondre." +
            "            </p>" +
            "        </div>" +
            "    </div>" +
            "</body>" +
            "</html>",
            userName, resetLink, resetLink, resetLink
        );

        // Envoyer l'email avec version HTML et texte
        mailer.send(Mail.withHtml(email, "Réinitialisation de votre mot de passe - Klaso", htmlBody)
                .setText(textBody));
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