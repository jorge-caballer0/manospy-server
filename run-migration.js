import pg from 'pg';
import fs from 'fs';

const { Pool } = pg;

const pool = new Pool({
  connectionString: process.env.DATABASE_URL || 'postgresql://postgres.yzxsbpiphsiuayfbvfxq:JorgeC5173380@aws-0-us-west-2.pooler.supabase.com:5432/postgres'
});

async function runMigration() {
  const client = await pool.connect();
  
  try {
    // Array of migrations to run in order
    const migrations = [
      './migrations/20260219_add_chats_and_chatid.sql',
      './migrations/20260223_fix_chats_client_id_type.sql'
    ];
    
    for (const migrationFile of migrations) {
      try {
        const migrationSQL = fs.readFileSync(migrationFile, 'utf8');
        console.log(`\n📋 Ejecutando: ${migrationFile}`);
        console.log(migrationSQL);
        
        await client.query(migrationSQL);
        console.log(`✅ ${migrationFile} - Exitosa`);
      } catch (fileErr) {
        if (fileErr.code === 'ENOENT') {
          console.warn(`⚠️ ${migrationFile} - No encontrado, saltando...`);
        } else {
          throw fileErr;
        }
      }
    }
    
    console.log('\n✅ Todas las migraciones completadas exitosamente');
  } catch (err) {
    console.error('❌ Error ejecutando migraciones:', err.message);
    process.exit(1);
  } finally {
    client.release();
    await pool.end();
  }
}

runMigration();
