CREATE TABLE IF NOT EXISTS app_data (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL CHECK (btrim(name) <> ''),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_data (name)
VALUES
    ('alpha'),
    ('bravo'),
    ('charlie');
