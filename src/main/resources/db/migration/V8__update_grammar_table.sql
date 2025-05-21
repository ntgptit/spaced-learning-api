-- V8__update_grammar_table.sql
ALTER TABLE spaced_learning.grammars RENAME COLUMN title TO grammar_pattern;
ALTER TABLE spaced_learning.grammars RENAME COLUMN explanation TO definition;
ALTER TABLE spaced_learning.grammars RENAME COLUMN usage_note TO structure;
ALTER TABLE spaced_learning.grammars ADD COLUMN conjugation TEXT;
ALTER TABLE spaced_learning.grammars ADD COLUMN common_phrases TEXT;
ALTER TABLE spaced_learning.grammars ADD COLUMN notes TEXT;

-- Rename example column to examples to match plural naming in JSON
ALTER TABLE spaced_learning.grammars RENAME COLUMN example TO examples;