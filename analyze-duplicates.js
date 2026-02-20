require('dotenv').config();
const { sequelize } = require('./models/index.js');

async function analyzeTablesAndDuplicates() {
  try {
    await sequelize.authenticate();
    console.log('✓ DB connected\n');

    // Listar todas las tablas
    const allTablesQuery = `
      SELECT table_name 
      FROM information_schema.tables 
      WHERE table_schema = 'public'
      ORDER BY table_name;
    `;
    
    const allTables = await sequelize.query(allTablesQuery, { type: 'SELECT' });
    console.log('📋 TODAS LAS TABLAS EN LA BD:\n');
    allTables.forEach(row => console.log(`  • ${row.table_name}`));

    // Detectar duplicados (ej: Messages/messages, Chats/chats)
    const tableNames = allTables.map(t => t.table_name);
    const lowerNames = tableNames.map(t => t.toLowerCase());
    
    console.log('\n🔍 DUPLICADOS DETECTADOS:\n');
    const seen = new Set();
    let hasDuplicates = false;
    
    tableNames.forEach(name => {
      const lower = name.toLowerCase();
      if (seen.has(lower)) {
        console.log(`  ⚠️  "${name}" (duplicado de tabla en diferente case)`);
        hasDuplicates = true;
      }
      seen.add(lower);
    });
    
    if (!hasDuplicates) {
      console.log('  ✓ No se encontraron duplicados por case');
    }

    // Analizar esquemas de tablas potencialmente duplicadas
    const potentialDuplicates = [
      { upper: 'Messages', lower: 'messages' },
      { upper: 'Chats', lower: 'chats' },
      { upper: 'PhoneVerifications', lower: 'phone_verifications' }
    ];

    console.log('\n📊 ANÁLISIS DE TABLAS POTENCIALMENTE DUPLICADAS:\n');
    
    for (const dup of potentialDuplicates) {
      const upperExists = tableNames.includes(dup.upper);
      const lowerExists = tableNames.includes(dup.lower);
      
      if (upperExists || lowerExists) {
        console.log(`\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`);
        console.log(`${dup.upper}/${dup.lower}:`);
        console.log(`━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`);
        
        if (upperExists) {
          console.log(`\n${dup.upper}:`);
          const schema = await sequelize.query(`
            SELECT column_name, data_type
            FROM information_schema.columns
            WHERE table_name = '${dup.upper}'
            ORDER BY ordinal_position;
          `, { type: 'SELECT' });
          
          schema.forEach(col => {
            console.log(`  • ${col.column_name}: ${col.data_type}`);
          });
          
          const count = await sequelize.query(`SELECT COUNT(*) as cnt FROM "${dup.upper}";`, { type: 'SELECT' });
          console.log(`  📈 Registros: ${count[0].cnt}`);
        }
        
        if (lowerExists) {
          console.log(`\n${dup.lower}:`);
          const schema = await sequelize.query(`
            SELECT column_name, data_type
            FROM information_schema.columns
            WHERE table_name = '${dup.lower}'
            ORDER BY ordinal_position;
          `, { type: 'SELECT' });
          
          schema.forEach(col => {
            console.log(`  • ${col.column_name}: ${col.data_type}`);
          });
          
          const count = await sequelize.query(`SELECT COUNT(*) as cnt FROM "${dup.lower}";`, { type: 'SELECT' });
          console.log(`  📈 Registros: ${count[0].cnt}`);
        }
      }
    }

    console.log('\n\n✅ RECOMENDACIÓN:\n');
    console.log('Ver análisis arriba para determinar cuál tabla está en uso (por count de registros)');
    console.log('Las tablas SIN registros pueden eliminarse.');

    process.exit(0);
  } catch (err) {
    console.error('ERROR:', err.message);
    process.exit(1);
  } finally {
    await sequelize.close();
  }
}

analyzeTablesAndDuplicates();
