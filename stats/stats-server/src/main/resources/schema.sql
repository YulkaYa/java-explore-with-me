CREATE TABLE IF NOT EXISTS endpoint_hits (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
app VARCHAR(255) NOT NULL,
uri VARCHAR(512) NOT NULL,
ip VARCHAR(15) NOT NULL,
timestamp TIMESTAMP WITHOUT TIME ZONE
);