CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE
);

-- The application inserts a starter task only when this table is empty.
-- Keep credentials and environment-specific values out of schema files.
