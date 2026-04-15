package com.klaso.service;

import com.klaso.dto.UserLoginDTO;
import com.klaso.dto.UserRegisterDTO;
import com.klaso.entity.Account;
import com.klaso.entity.Profile;
import com.klaso.entity.User;
import com.klaso.repository.AccountRepository;
import com.klaso.repository.ProfileRepository;
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
    AccountRepository accountRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    Mailer mailer;

//    @Transactional
//    public User register(UserRegisterDTO dto) {
//        User user = new User();
//        user.setEmail(dto.email);
//        user.setFirstName(dto.firstName);
//        user.setLastName(dto.lastName);
//        user.setPassword(BCrypt.hashpw(dto.password, BCrypt.gensalt()));
//        user.setCreatedAt(Instant.now());
//        user.setUpdatedAt(Instant.now());
//        userRepository.persist(user);
//        return user;
//    }

    // ... existing code ...
    @Transactional
    public Account register(UserRegisterDTO dto) {
        // 1. Créer l'utilisateur (personne)
        User user = new User();
        user.setEmail(dto.email);
        user.setFirstName(dto.firstName);
        user.setLastName(dto.lastName);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.persist(user);

        // 2. Récupérer le profil choisi (ou TEACHER par défaut)
        String roleCode = (dto.role != null && !dto.role.isBlank()) ? dto.role.toUpperCase() : "TEACHER";
        Profile profile = profileRepository.findByCode(roleCode);
        if (profile == null) {
            throw new WebApplicationException("Profil '" + roleCode + "' introuvable", Response.Status.BAD_REQUEST);
        }

        // 3. Créer le compte
        Account account = new Account();
        account.setUser(user);
        account.setProfile(profile);
        account.setUsername(dto.email); // login = email
        account.setPasswordHash(BCrypt.hashpw(dto.password, BCrypt.gensalt()));
        account.setIsActive(true);
        account.setDeleted(false);
        account.setCreatedAt(Instant.now());
        account.setUpdatedAt(Instant.now());
        accountRepository.persist(account);

        return account;
    }

//    public String login(UserLoginDTO dto) {
//        User user = userRepository.findByEmail(dto.email);
//        if (user == null || !BCrypt.checkpw(dto.password, user.getPassword())) {
//            throw new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED);
//        }
//        return Jwt.issuer("klaso")
//                .upn(user.getEmail())
//                .claim("id", user.getId())
//                .claim("email", user.getEmail())
//                .claim("name", user.getFirstName() + " " + user.getLastName())
//                .sign();
//    }

    // ... existing code ...
    public String login(UserLoginDTO dto) {
        // 1. Récupérer le compte par username (email)
        Account account = accountRepository.findByUsername(dto.email);
        if (account == null || !Boolean.TRUE.equals(account.getIsActive()) || Boolean.TRUE.equals(account.getDeleted())) {
            throw new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED);
        }

        // 2. Vérifier le mot de passe
        if (!BCrypt.checkpw(dto.password, account.getPasswordHash())) {
            // Optionnel: incrémenter connectionAttempt, bloquer après X essais
            throw new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED);
        }

        User user = account.getUser();
        Profile profile = account.getProfile();

        // 3. Construire le JWT avec l'info de profil
        return Jwt.issuer("klaso")
                .upn(account.getUsername())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getFirstName() + " " + user.getLastName())
                .groups(profile.getCode()) // Utilise 'groups' pour les @RolesAllowed de Quarkus
                .sign();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void sendPasswordResetEmail(String email) {
        Account account = accountRepository.findByUsername(email);
        if (account == null) {
            // Pour des raisons de sécurité, on ne révèle pas si l'email existe ou non
            return;
        }

        User targetUser = account.getUser();

        // Générer un token unique
        String resetToken = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(3600); // Token valide 1 heure

        // Sauvegarder le token dans le compte
        account.setResetToken(resetToken);
        account.setResetTokenExpiresAt(expiresAt);
        account.setUpdatedAt(Instant.now());
        accountRepository.persist(account);

        // Envoyer l'email
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
        String userName = targetUser.getFirstName() != null ? targetUser.getFirstName() : "Utilisateur";
        
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
        Account account = accountRepository.find("resetToken", token).firstResult();
        
        if (account == null || account.getResetTokenExpiresAt() == null || 
            account.getResetTokenExpiresAt().isBefore(Instant.now())) {
            throw new WebApplicationException("Token invalide ou expiré", Response.Status.BAD_REQUEST);
        }

        // Mettre à jour le mot de passe
        account.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        account.setResetToken(null);
        account.setResetTokenExpiresAt(null);
        account.setUpdatedAt(Instant.now());
        accountRepository.persist(account);
    }

    // --- Admin Features ---

    public java.util.List<Account> getAllAccounts() {
        return accountRepository.find("deleted", false).list();
    }

    @Transactional
    public void toggleBlock(Long accountId) {
        Account account = accountRepository.findById(accountId);
        if (account == null) throw new jakarta.ws.rs.WebApplicationException("Compte introuvable", jakarta.ws.rs.core.Response.Status.NOT_FOUND);
        account.setIsActive(!Boolean.TRUE.equals(account.getIsActive()));
        account.setUpdatedAt(java.time.Instant.now());
    }

    @Transactional
    public void softDelete(Long accountId) {
        Account account = accountRepository.findById(accountId);
        if (account == null) throw new jakarta.ws.rs.WebApplicationException("Compte introuvable", jakarta.ws.rs.core.Response.Status.NOT_FOUND);
        account.setDeleted(true);
        account.setUpdatedAt(java.time.Instant.now());
    }
}