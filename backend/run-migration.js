import pg from 'pg';
import fs from 'fs';

const { Pool } = pg;

const pool = new Pool({
  connectionString: process.env.DATABASE_URL || 'postgresql://postgres.yzxsbpiphsiuayfbvfxq:JorgeC5173380@aws-0-us-west-2.pooler.supabase.com:5432/postgres'
});

async function runMigration() {
  const client = await pool.connect();
  
  try {
    // Read the migration SQL file
    const migrationSQL = fs.readFileSync('./migrations/20260219_add_chats_and_chatid.sql', 'utf8');
    
    console.log('Ejecutando migración...');
    console.log(migrationSQL);
    
    // Execute the migration
    await client.query(migrationSQL);
    
    console.log('✅ Migración ejecutada exitosamente');
  } catch (err) {
    console.error('❌ Error ejecutando migración:', err.message);
    process.exit(1);
  } finally {
    client.release();
    await pool.end();
  }
}

runMigration();
