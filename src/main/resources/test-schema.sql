TRUNCATE TABLE station;
ALTER TABLE station
    ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE line;
ALTER TABLE line
    ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE section;
ALTER TABLE section
    ALTER COLUMN id RESTART WITH 1;