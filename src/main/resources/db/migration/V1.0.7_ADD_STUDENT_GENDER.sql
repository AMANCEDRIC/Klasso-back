--liquibase formatted sql

--changeset aman:26

ALTER TABLE student ADD COLUMN gender VARCHAR(10) AFTER last_name;