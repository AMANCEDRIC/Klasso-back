--liquibase formatted sql

--changeset aman:2
--comment: Ajout des champs pour la réinitialisation de mot de passe

-- Ajouter les colonnes pour la réinitialisation de mot de passe
ALTER TABLE users 
ADD COLUMN reset_token VARCHAR(255) NULL,
ADD COLUMN reset_token_expires_at TIMESTAMP NULL;

-- Ajouter un index sur le token pour améliorer les performances
CREATE INDEX idx_users_reset_token ON users(reset_token);
