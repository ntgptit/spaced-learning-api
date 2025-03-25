-- Create schema
CREATE SCHEMA spaced_learning;

-- Set search path
SET search_path TO spaced_learning, public;

-- 1. Extension UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Bảng người dùng
CREATE TABLE spaced_learning.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP
);

-- 3. Bảng vai trò
CREATE TABLE spaced_learning.roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 4. Bảng liên kết người dùng và vai trò
CREATE TABLE spaced_learning.user_roles (
    user_id UUID NOT NULL REFERENCES spaced_learning.users(id),
    role_id BIGINT NOT NULL REFERENCES spaced_learning.roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- 5. Bảng sách học
CREATE TABLE spaced_learning.books (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('PUBLISHED', 'DRAFT', 'ARCHIVED')),
    difficulty_level VARCHAR(20) CHECK (difficulty_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP
);

-- 6. Bảng học phần (modules)
CREATE TABLE spaced_learning.modules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    book_id UUID NOT NULL REFERENCES spaced_learning.books(id) ON DELETE CASCADE,
    module_no INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    word_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP,
    CONSTRAINT unique_module_in_book UNIQUE (book_id, module_no)
);

-- 7. Bảng tiến độ học từng học phần
CREATE TABLE spaced_learning.module_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    module_id UUID NOT NULL REFERENCES spaced_learning.modules(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES spaced_learning.users(id) ON DELETE CASCADE,
    first_learning_date DATE,
    cycles_studied VARCHAR(30) DEFAULT 'FIRST_TIME' CHECK (cycles_studied IN ('FIRST_TIME', 'FIRST_REVIEW', 'SECOND_REVIEW', 'THIRD_REVIEW', 'MORE_THAN_THREE_REVIEWS')),
    next_study_date DATE,
    percent_complete DECIMAL(5,2) DEFAULT 0 CHECK (percent_complete BETWEEN 0 AND 100),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP,
    CONSTRAINT check_dates CHECK (next_study_date IS NULL OR first_learning_date IS NULL OR next_study_date >= first_learning_date),
    CONSTRAINT unique_user_module UNIQUE (user_id, module_id)
);

-- 8. Bảng ôn tập (Spaced Repetition chi tiết)
CREATE TABLE spaced_learning.repetitions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    module_progress_id UUID NOT NULL REFERENCES spaced_learning.module_progress(id) ON DELETE CASCADE,
    repetition_order VARCHAR(20) NOT NULL CHECK (repetition_order IN ('FIRST_REPETITION', 'SECOND_REPETITION', 'THIRD_REPETITION', 'FOURTH_REPETITION', 'FIFTH_REPETITION')),
    status VARCHAR(50) CHECK (status IN ('NOT_STARTED', 'COMPLETED', 'SKIPPED')),
    review_date DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP,
    CONSTRAINT unique_repetition_order UNIQUE (module_progress_id, repetition_order)
);

-- 9. Indexes
CREATE INDEX idx_module_progress_user_date ON spaced_learning.module_progress(user_id, next_study_date);
CREATE INDEX idx_repetitions_order ON spaced_learning.repetitions(module_progress_id, repetition_order);
CREATE INDEX idx_modules_book_id ON spaced_learning.modules(book_id);

-- 10. Initial data
-- Insert default roles
INSERT INTO spaced_learning.roles (name, description) VALUES 
('ROLE_USER', 'Standard user role'),
('ROLE_ADMIN', 'Administrator role');

-- Insert default admin user (password: admin123)
INSERT INTO spaced_learning.users (id, name, email, password, status) 
VALUES (uuid_generate_v4(), 'Admin User', 'admin@example.com', '$2a$12$SJB3XVCh1NrBJovcO1.IQetYRnPfADEflFD9S3HmYc.SdjrUn17Oa', 'ACTIVE');

-- Assign admin role to admin user
INSERT INTO spaced_learning.user_roles (user_id, role_id)
SELECT u.id, r.id FROM spaced_learning.users u, spaced_learning.roles r
WHERE u.email = 'admin@example.com' AND r.name = 'ROLE_ADMIN';
