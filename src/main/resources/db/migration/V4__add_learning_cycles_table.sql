-- Create learning cycles table for tracking study cycle history
CREATE TABLE IF NOT EXISTS spaced_learning.learning_cycles (
    id uuid NOT NULL,
    module_progress_id uuid NOT NULL,
    cycles_studied varchar(30) NOT NULL,
    start_date date NOT NULL,
    description varchar(255),
    created_at timestamp(6) NULL,
    updated_at timestamp(6) NULL,
    deleted_at timestamp(6) NULL,
    CONSTRAINT learning_cycles_pkey PRIMARY KEY (id),
    CONSTRAINT uk_learning_cycles_progress_cycle_date UNIQUE (module_progress_id, cycles_studied, start_date),
    CONSTRAINT fk_learning_cycles_module_progress FOREIGN KEY (module_progress_id) 
        REFERENCES spaced_learning.module_progress(id)
);

-- Create index for efficient lookup by module_progress_id
CREATE INDEX IF NOT EXISTS idx_learning_cycles_progress_id ON spaced_learning.learning_cycles(module_progress_id);

-- Create index for efficient lookup by cycles_studied
CREATE INDEX IF NOT EXISTS idx_learning_cycles_cycle ON spaced_learning.learning_cycles(cycles_studied);

-- Create index for efficient lookup by start_date
CREATE INDEX IF NOT EXISTS idx_learning_cycles_start_date ON spaced_learning.learning_cycles(start_date);

-- Add comment to table for documentation
COMMENT ON TABLE spaced_learning.learning_cycles IS 'Stores history of learning cycles for each module progress record';

-- Add comments to columns for documentation
COMMENT ON COLUMN spaced_learning.learning_cycles.module_progress_id IS 'Reference to the module progress';
COMMENT ON COLUMN spaced_learning.learning_cycles.cycles_studied IS 'Type of learning cycle';
COMMENT ON COLUMN spaced_learning.learning_cycles.start_date IS 'Date when this learning cycle started';
COMMENT ON COLUMN spaced_learning.learning_cycles.description IS 'Optional description for this cycle';