require('dotenv').config();
const { sequelize } = require('./models/index.js');

async function listAllTables() {
  try {
    await sequelize.authenticate();
    console.log('✓ DB connected\n');

    // Listar todas las tablas
    const query = `
      SELECT table_name 
      FROM information_schema.tables 
      WHERE table_schema = 'public'
      ORDER BY table_name;
    `;
    
    const tables = await sequelize.query(query, { type: 'SELECT' });
    console.log('📋 TODAS LAS TABLAS EN LA BD:\n');
    tables.forEach(row => {
      console.log(`  • ${row.table_name}`);
    });

    // Buscar tablas relacionadas con ofertas
    console.log('\n🔍 Tablas potenciales para OFERTAS:');
    const tableNames = tables.map(t => t.table_name.toLowerCase());
    const potentialOfferTables = tableNames.filter(t => 
      t.includes('offer') || t.includes('promot') || t.includes('anunci') || t.includes('product') || t.includes('service')
    );
    
    if (potentialOfferTables.length > 0) {
      potentialOfferTables.forEach(t => {
        console.log(`  • ${t}`);
      });
    } else {
      console.log('  ⚠️  No se encontraron tablas de ofertas/promociones');
    }

    process.exit(0);
  } catch (err) {
    console.error('ERROR:', err.message);
    process.exit(1);
  } finally {
    await sequelize.close();
  }
}

listAllTables();
