require('dotenv').config();
const { sequelize } = require('./models/index.js');

async function createProfessionalOffersTable() {
  try {
    await sequelize.authenticate();
    console.log('✓ DB connected\n');

    // 1. Crear tabla ProfessionalOffers
    console.log('📝 Creando tabla ProfessionalOffers...');
    await sequelize.query(`
      CREATE TABLE IF NOT EXISTS "ProfessionalOffers" (
        id SERIAL PRIMARY KEY,
        "professionalId" INTEGER NOT NULL REFERENCES "Users"(id) ON DELETE CASCADE,
        title VARCHAR(255) NOT NULL,
        description TEXT NOT NULL,
        category VARCHAR(100) NOT NULL,
        location VARCHAR(255),
        price DECIMAL(10, 2),
        currency VARCHAR(10) DEFAULT 'ARS',
        availability JSONB DEFAULT '{"days": [], "hours": ""}',
        status VARCHAR(50) DEFAULT 'active',
        image_url TEXT,
        "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
      );
    `);
    console.log('✓ Tabla ProfessionalOffers creada');

    // 2. Eliminar tablas duplicadas vacías
    console.log('\n🗑️  Eliminando tablas duplicadas...');
    
    try {
      await sequelize.query(`DROP TABLE IF EXISTS "chats";`);
      console.log('✓ Tabla "chats" (snake_case) eliminada');
    } catch (e) {
      console.log('  (chats ya no existe)');
    }

    try {
      await sequelize.query(`DROP TABLE IF EXISTS "phone_verifications";`);
      console.log('✓ Tabla "phone_verifications" (snake_case) eliminada');
    } catch (e) {
      console.log('  (phone_verifications ya no existe)');
    }

    console.log('\n✅ Base de datos limpiada y actualizada');
    console.log('\n📊 Resumen:');
    console.log('  ✓ ProfessionalOffers: tabla nueva para ofertas de profesionales');
    console.log('  ✓ Chats: tabla única para chats (eliminada duplicate)');
    console.log('  ✓ PhoneVerifications: tabla única (eliminada duplicate)');

    process.exit(0);
  } catch (err) {
    console.error('ERROR:', err.message);
    process.exit(1);
  } finally {
    await sequelize.close();
  }
}

createProfessionalOffersTable();
