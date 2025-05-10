-- V5__add_extended_review_count_to_module_progress.sql

ALTER TABLE spaced_learning.module_progress
ADD COLUMN extended_review_count INT DEFAULT 0;
