-- V10: Change Logs.ip from INET to VARCHAR(45) so Hibernate can persist String values.
ALTER TABLE Logs ALTER COLUMN ip TYPE VARCHAR(45);
