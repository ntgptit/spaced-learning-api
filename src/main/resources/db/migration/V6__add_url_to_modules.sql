-- Add URL column to modules table
ALTER TABLE spaced_learning.modules 
ADD COLUMN url VARCHAR(500);

-- Add comment for documentation
COMMENT ON COLUMN spaced_learning.modules.url IS 'URL to the module web page';