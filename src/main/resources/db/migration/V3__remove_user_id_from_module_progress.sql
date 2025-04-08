-- src/main/resources/db/migration/V3__remove_user_id_from_module_progress.sql
ALTER TABLE spaced_learning.module_progress DROP COLUMN IF EXISTS user_id;