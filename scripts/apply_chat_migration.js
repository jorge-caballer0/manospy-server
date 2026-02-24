import pg from 'pg';
import dotenv from 'dotenv';

dotenv.config();

const { Pool } = pg;

async function applyMigration() {
  const connectionString = process.env.DATABASE_URL;
  if (!connectionString) {
    console.error('DATABASE_URL no está definido. Exporta DATABASE_URL antes de ejecutar.');
    process.exit(1);
  }

  const pool = new Pool({ connectionString });
  const client = await pool.connect();

  try {
    console.log('🔎 Comprobando tipos de columnas en tabla "chats"...');
    const checkRes = await client.query(`
      SELECT column_name, data_type
      FROM information_schema.columns
      WHERE table_name='chats' AND column_name IN ('client_id','professional_id')
    `);

    const rows = checkRes.rows.reduce((acc, r) => {
      acc[r.column_name] = r.data_type;
      return acc;
    }, {});

    console.log('Tipos actuales:', rows);

    const needClient = rows['client_id'] && rows['client_id'] !== 'text';
    const needProf = rows['professional_id'] && rows['professional_id'] !== 'text';

    if (!needClient && !needProf) {
      console.log('✅ Las columnas ya están en tipo TEXT o no existen. No se requiere acción.');
      return;
    }

    console.log('🛠️ Aplicando cambios (si es necesario)...');
    await client.query('BEGIN');

    if (needClient) {
      console.log(' - Alterando client_id a TEXT (usando client_id::text)');
      await client.query("ALTER TABLE chats ALTER COLUMN client_id TYPE text USING client_id::text;");
    }

    if (needProf) {
      console.log(' - Alterando professional_id a TEXT (usando professional_id::text)');
      await client.query("ALTER TABLE chats ALTER COLUMN professional_id TYPE text USING professional_id::text;");
    }

    await client.query('COMMIT');
    console.log('✅ Migración aplicada correctamente.');

    // Verificar de nuevo
    const after = await client.query(`
      SELECT column_name, data_type
      FROM information_schema.columns
      WHERE table_name='chats' AND column_name IN ('client_id','professional_id')
    `);
    console.log('Tipos posteriores:', after.rows);
  } catch (err) {
    console.error('❌ Error aplicando migración:', err.message || err);
    try { await client.query('ROLLBACK'); } catch (e) {}
    process.exit(1);
  } finally {
    client.release();
    await pool.end();
  }
}

applyMigration();

// Uso:
// 1) Exporta DATABASE_URL en tu entorno (o crea un .env con DATABASE_URL)
// 2) Ejecuta: node backend/scripts/apply_chat_migration.js
