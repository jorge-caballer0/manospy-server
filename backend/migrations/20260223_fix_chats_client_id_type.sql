-- Migration: Fix client_id column type from UUID to TEXT
-- This addresses type mismatch where User.id is INTEGER but client_id was UUID
-- Run with psql against your database

BEGIN;

-- 1) Convert client_id column from UUID to TEXT to match User.id type
ALTER TABLE IF EXISTS chats ALTER COLUMN client_id TYPE TEXT;

-- 2) Convert professional_id column from UUID to TEXT for consistency
ALTER TABLE IF EXISTS chats ALTER COLUMN professional_id TYPE TEXT;

COMMIT;
