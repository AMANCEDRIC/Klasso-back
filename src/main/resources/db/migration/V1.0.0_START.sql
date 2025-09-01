--liquibase formatted sql

--changeset aman:1

-- 🔧 Désactivation des contraintes pour l'import
SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
  'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- 🧱 Création des tables (ordre important)

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       password VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE establishment (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               name VARCHAR(255),
                               address VARCHAR(255),
                               city VARCHAR(100),
                               postal_code VARCHAR(20),
                               country VARCHAR(100),
                               user_id BIGINT,
                               created_at TIMESTAMP,
                               updated_at TIMESTAMP,
                               CONSTRAINT fk_establishment_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE classroom (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(50),
                           level VARCHAR(50),
                           subject VARCHAR(100),
                           academic_year VARCHAR(20),
                           establishment_id BIGINT,
                           user_id BIGINT,
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           CONSTRAINT fk_classroom_establishment FOREIGN KEY (establishment_id) REFERENCES establishment(id),
                           CONSTRAINT fk_classroom_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE student (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         first_name VARCHAR(100),
                         last_name VARCHAR(100),
                         date_of_birth DATE,
                         email VARCHAR(255),
                         parent_name VARCHAR(255),
                         parent_email VARCHAR(255),
                         parent_phone VARCHAR(20),
                         classroom_id BIGINT,
                         enrollment_date DATE,
                         is_active BOOLEAN,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP,
                         CONSTRAINT fk_student_classroom FOREIGN KEY (classroom_id) REFERENCES classroom(id)
);

CREATE TABLE grade (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       value DECIMAL(5,2),
                       max_value DECIMAL(5,2),
                       coefficient INT,
                       grade_type VARCHAR(50),
                       subject VARCHAR(100),
                       description TEXT,
                       grade_date DATE,
                       student_id BIGINT,
                       classroom_id BIGINT,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       CONSTRAINT fk_grade_student FOREIGN KEY (student_id) REFERENCES student(id),
                       CONSTRAINT fk_grade_classroom FOREIGN KEY (classroom_id) REFERENCES classroom(id)
);

-- ✅ Insertion des données initiales

-- 👤 Utilisateur de test
INSERT INTO users (id, email, first_name, last_name, password, created_at, updated_at)
VALUES (1, 'prof@klaso.com', 'Naounou', 'France Liliane', 'password123', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 🏫 Établissement
INSERT INTO establishment (id, name, address, city, postal_code, country, user_id, created_at, updated_at)
VALUES (1, 'Collège Victor Hugo', '123 rue de la République', 'Abidjan', '75001', 'cote d ivoire', 1, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 🏛️ Classe
INSERT INTO classroom (id, name, level, subject, academic_year, establishment_id, user_id, created_at, updated_at)
VALUES (1, '6ème A', '6ème', 'Mathématiques', '2024-2025', 1, 1, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 👧👦 Élèves
INSERT INTO student (id, first_name, last_name, date_of_birth, email, parent_name, parent_email, parent_phone, classroom_id, enrollment_date, is_active, created_at, updated_at)
VALUES
    (1, 'Marie', 'Martin', '2012-03-15', 'marie.martin@email.com', 'Pierre Martin', 'pierre.martin@email.com', '0123456789', 1, '2024-09-01', true, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    (2, 'Lucas', 'Durand', '2012-05-20', NULL, 'Sophie Durand', 'sophie.durand@email.com', '0123456790', 1, '2024-09-01', true, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 📝 Notes
INSERT INTO grade (id, value, max_value, coefficient, grade_type, subject, description, grade_date, student_id, classroom_id, created_at, updated_at)
VALUES
    (1, 15, 20, 1, 'TEST', 'Mathématiques', 'Contrôle sur les fractions', '2024-10-15', 1, 1, '2024-10-15', '2024-10-15'),
    (2, 12, 20, 1, 'TEST', 'Mathématiques', 'Contrôle sur les fractions', '2024-10-15', 2, 1, '2024-10-15', '2024-10-15');

-- ✅ Restauration des paramètres
SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
