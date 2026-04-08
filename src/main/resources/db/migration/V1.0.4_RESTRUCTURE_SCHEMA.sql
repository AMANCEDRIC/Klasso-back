--liquibase formatted sql

--changeset aman:17
-- 1. Création de la table period
CREATE TABLE period (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    establishment_id BIGINT NOT NULL,
    name VARCHAR(100),
    type VARCHAR(50),
    number INT,
    start_date DATE,
    end_date DATE,
    academic_year VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_period_establishment FOREIGN KEY (establishment_id) REFERENCES establishment(id)
);

--changeset aman:18
-- 2. Mise à jour de establishment (ajout du type de découpage)
ALTER TABLE establishment ADD COLUMN period_type VARCHAR(50);
ALTER TABLE establishment ADD COLUMN academic_year VARCHAR(20);

--changeset aman:19
-- 3. Mise à jour des tables IAM (Security) : Déplacement du reset_token
ALTER TABLE account ADD COLUMN reset_token VARCHAR(255);
ALTER TABLE account ADD COLUMN reset_token_expires_at TIMESTAMP NULL;

--changeset aman:20
-- 4. Retrait des champs d'authentification de la table users
-- Supprimer l'index sur reset_token AVANT de supprimer la colonne (créé en V1.0.1)
DROP INDEX idx_users_reset_token ON users;
ALTER TABLE users DROP COLUMN password;
ALTER TABLE users DROP COLUMN reset_token;
ALTER TABLE users DROP COLUMN reset_token_expires_at;

--changeset aman:21
-- 5. Nettoyage et mise à jour de la table grade (notes)
-- On retire l'ancienne clé étrangère vers classroom
ALTER TABLE grade DROP FOREIGN KEY fk_grade_classroom;
-- On supprime toutes les colonnes devenues redondantes
ALTER TABLE grade DROP COLUMN grade_type;
ALTER TABLE grade DROP COLUMN subject;
ALTER TABLE grade DROP COLUMN max_value;
ALTER TABLE grade DROP COLUMN coefficient;
ALTER TABLE grade DROP COLUMN description;
ALTER TABLE grade DROP COLUMN grade_date;
ALTER TABLE grade DROP COLUMN classroom_id;
-- On ajoute les nouveaux champs
ALTER TABLE grade ADD COLUMN is_absent BOOLEAN DEFAULT FALSE;
ALTER TABLE grade ADD COLUMN appreciation TEXT;

--changeset aman:22
-- 6. Mise à jour de la table evaluation
-- Ajouter la FK manquante sur classroom_id (V1.0.2 n'avait créé qu'un index)
ALTER TABLE evaluation ADD CONSTRAINT fk_evaluation_classroom FOREIGN KEY (classroom_id) REFERENCES classroom(id);
-- Lier à la period
ALTER TABLE evaluation ADD COLUMN period_id BIGINT;
ALTER TABLE evaluation ADD CONSTRAINT fk_evaluation_period FOREIGN KEY (period_id) REFERENCES period(id);
-- Supprimer subject (redondant avec classroom.subject)
ALTER TABLE evaluation DROP COLUMN subject;
