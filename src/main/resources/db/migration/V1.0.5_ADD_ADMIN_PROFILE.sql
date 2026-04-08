--liquibase formatted sql

--changeset aman:23
-- Ajout du profil ADMIN
INSERT INTO profile (code, label)
VALUES ('ADMIN', 'Administrateur');

--changeset aman:24
-- Associer toutes les permissions au profil ADMIN
INSERT INTO profile_permission (profile_id, permission_id)
SELECT
    (SELECT id FROM profile WHERE code = 'ADMIN') AS profile_id,
    p.id AS permission_id
FROM permission p;
