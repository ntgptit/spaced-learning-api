-- V7__add_vocabulary_and_grammar_tables.sql
-- Create vocabularies table
CREATE TABLE IF NOT EXISTS spaced_learning.vocabularies (
                                                            id uuid NOT NULL,
                                                            module_id uuid NOT NULL,
                                                            term varchar(100) NOT NULL,
    definition text,
    example text,
    pronunciation varchar(100),
    part_of_speech varchar(100) NOT NULL,
    created_at timestamp(6) NULL,
    updated_at timestamp(6) NULL,
    deleted_at timestamp(6) NULL,
    CONSTRAINT vocabularies_pkey PRIMARY KEY (id),
    CONSTRAINT fk_vocabularies_module FOREIGN KEY (module_id) REFERENCES spaced_learning.modules(id)
    );

-- Create indexes for vocabularies
CREATE INDEX IF NOT EXISTS idx_vocabularies_module_id ON spaced_learning.vocabularies(module_id);
CREATE INDEX IF NOT EXISTS idx_vocabularies_term ON spaced_learning.vocabularies(term);
CREATE INDEX IF NOT EXISTS idx_vocabularies_part_of_speech ON spaced_learning.vocabularies(part_of_speech);

-- Create grammars table
CREATE TABLE IF NOT EXISTS spaced_learning.grammars (
                                                        id uuid NOT NULL,
                                                        module_id uuid NOT NULL,
                                                        title varchar(100) NOT NULL,
    explanation text,
    usage_note text,
    example text,
    created_at timestamp(6) NULL,
    updated_at timestamp(6) NULL,
    deleted_at timestamp(6) NULL,
    CONSTRAINT grammars_pkey PRIMARY KEY (id),
    CONSTRAINT fk_grammars_module FOREIGN KEY (module_id) REFERENCES spaced_learning.modules(id)
    );

-- Create indexes for grammars
CREATE INDEX IF NOT EXISTS idx_grammars_module_id ON spaced_learning.grammars(module_id);
CREATE INDEX IF NOT EXISTS idx_grammars_title ON spaced_learning.grammars(title);

-- Add comments for documentation
COMMENT ON TABLE spaced_learning.vocabularies IS 'Stores vocabulary terms associated with modules';
COMMENT ON TABLE spaced_learning.grammars IS 'Stores grammar rules associated with modules';