-- Create user_books junction table for many-to-many relationship
CREATE TABLE IF NOT EXISTS spaced_learning.user_books (
    user_id uuid NOT NULL,
    book_id uuid NOT NULL,
    created_at timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_books_pkey PRIMARY KEY (user_id, book_id),
    CONSTRAINT fk_user_books_user FOREIGN KEY (user_id) REFERENCES spaced_learning.users(id),
    CONSTRAINT fk_user_books_book FOREIGN KEY (book_id) REFERENCES spaced_learning.books(id)
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_user_books_user_id ON spaced_learning.user_books(user_id);
CREATE INDEX IF NOT EXISTS idx_user_books_book_id ON spaced_learning.user_books(book_id);

-- Update module_progress table to change relationship structure
ALTER TABLE spaced_learning.module_progress 
ALTER COLUMN user_id TYPE uuid USING user_id::uuid;