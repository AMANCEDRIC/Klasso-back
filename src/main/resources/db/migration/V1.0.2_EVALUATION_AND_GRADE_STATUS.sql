--liquibase formatted sql

--changeset aman:3
CREATE TABLE evaluation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  classroom_id BIGINT NOT NULL,
  subject VARCHAR(100),
  grade_type VARCHAR(50),
  max_value DECIMAL(5,2),
  coefficient INT,
  evaluation_date DATE,
  description TEXT,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);

--changeset aman:4
CREATE INDEX idx_evaluation_classroom ON evaluation(classroom_id);

--changeset aman:5
ALTER TABLE grade ADD COLUMN evaluation_id BIGINT NULL;

--changeset aman:6
ALTER TABLE grade ADD COLUMN status VARCHAR(32) NULL;

--changeset aman:7
CREATE INDEX idx_grade_evaluation ON grade(evaluation_id);

--changeset aman:8
CREATE INDEX idx_grade_student ON grade(student_id);
