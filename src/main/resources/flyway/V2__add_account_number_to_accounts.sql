-- Add account_number column to accounts table
ALTER TABLE accounts
    ADD COLUMN account_number VARCHAR(20) NOT NULL UNIQUE;