-- Create airports_data table for H2
CREATE TABLE IF NOT EXISTS airports_data (
    airport_code CHAR(3) PRIMARY KEY,
    airport_name JSON,
    city JSON,
    country JSON,
    coordinates VARCHAR,
    timezone TEXT
);

