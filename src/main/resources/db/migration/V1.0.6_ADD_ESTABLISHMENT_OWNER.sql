--liquibase formatted sql

--changeset aman:25
-- 1. Ajout de la colonne account_id à la table establishment
ALTER TABLE establishment ADD COLUMN account_id BIGINT;

-- 2. Ajout de la contrainte de clé étrangère
ALTER TABLE establishment ADD CONSTRAINT fk_establishment_account FOREIGN KEY (account_id) REFERENCES account(id);

-- 3. Index pour optimiser les recherches par propriétaire
CREATE INDEX idx_establishment_account ON establishment(account_id);
