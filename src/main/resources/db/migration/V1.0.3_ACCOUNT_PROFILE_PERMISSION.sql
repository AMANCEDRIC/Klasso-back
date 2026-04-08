--liquibase formatted sql

--changeset aman:9
SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
  'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
-- Création de la table profile (rôle)
CREATE TABLE profile (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         code VARCHAR(50) NOT NULL UNIQUE,
                         label VARCHAR(100) NOT NULL,
                         is_active TINYINT(1) DEFAULT 1,
                         deleted TINYINT(1) DEFAULT 0,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--changeset aman:10
-- Création de la table permission
CREATE TABLE permission (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            code VARCHAR(100) NOT NULL UNIQUE,
                            label VARCHAR(255) NOT NULL,
                            is_active TINYINT(1) DEFAULT 1,
                            deleted TINYINT(1) DEFAULT 0,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--changeset aman:11
-- Table de jointure profile <-> permission
CREATE TABLE profile_permission (
                                    profile_id BIGINT NOT NULL,
                                    permission_id BIGINT NOT NULL,
                                    PRIMARY KEY (profile_id, permission_id),
                                    CONSTRAINT fk_profile_permission_profile
                                        FOREIGN KEY (profile_id) REFERENCES profile(id),
                                    CONSTRAINT fk_profile_permission_permission
                                        FOREIGN KEY (permission_id) REFERENCES permission(id)
);

--changeset aman:12
-- Création de la table account
CREATE TABLE account (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         profile_id BIGINT NOT NULL,
                         username VARCHAR(200) NOT NULL UNIQUE,
                         password_hash VARCHAR(255) NOT NULL,
                         session_token VARCHAR(255) NULL,
                         connection_attempt INT DEFAULT 0,
                         last_connected_at DATETIME NULL,
                         is_active TINYINT(1) DEFAULT 1,
                         deleted TINYINT(1) DEFAULT 0,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(id),
                         CONSTRAINT fk_account_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

--changeset aman:13

-- Profil par défaut "TEACHER"
INSERT INTO profile (code, label)
VALUES ('TEACHER', 'Enseignant');

--changeset aman:14

-- (Optionnel) Quelques permissions de base
INSERT INTO permission (code, label)
VALUES
    ('VIEW_GRADES', 'Voir les notes'),
    ('EDIT_GRADES', 'Modifier les notes'),
    ('MANAGE_STUDENTS', 'Gérer les élèves');

--changeset aman:15
-- (Optionnel) Associer toutes les permissions au profil TEACHER
INSERT INTO profile_permission (profile_id, permission_id)
SELECT
    (SELECT id FROM profile WHERE code = 'TEACHER') AS profile_id,
    p.id AS permission_id
FROM permission p;

--changeset aman:16
-- Migration des utilisateurs existants vers account
INSERT INTO account (user_id, profile_id, username, password_hash, is_active, created_at, updated_at)
SELECT
    u.id,
    (SELECT id FROM profile WHERE code = 'TEACHER') AS profile_id,
    u.email AS username,
    u.password AS password_hash,
    1 AS is_active,
    u.created_at,
    u.updated_at
FROM users u;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;