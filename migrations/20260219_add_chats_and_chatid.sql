-- Migration: Add chats table and allow messages without reservationId
-- Run with psql against your database. Review before applying in production.

BEGIN;

-- 1) Crear tabla chats
CREATE TABLE IF NOT EXISTS chats (
  id TEXT PRIMARY KEY,
  offer_id TEXT,
  client_id UUID,
  professional_id UUID
);

-- 2) Añadir columna chat_id a messages (si no existe)
ALTER TABLE IF EXISTS "Messages" ADD COLUMN IF NOT EXISTS chat_id TEXT;

-- 3) Permitir reservationId nulo
ALTER TABLE IF EXISTS "Messages" ALTER COLUMN "reservationId" DROP NOT NULL;

COMMIT;
