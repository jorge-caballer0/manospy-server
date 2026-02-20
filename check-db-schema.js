require('dotenv').config();
const { sequelize } = require('./models/index.js');

async function checkSchema() {
  try {
    await sequelize.authenticate();
    console.log('✓ DB connected\n');

    // Consultar schema de ServiceRequests
    const query = `
      SELECT column_name, data_type
      FROM information_schema.columns
      WHERE table_name = 'ServiceRequests'
      ORDER BY ordinal_position;
    `;
    
    const result = await sequelize.query(query, { type: 'SELECT' });
    console.log('ServiceRequests schema:');
    result.forEach(row => {
      console.log(`  ${row.column_name}: ${row.data_type}`);
    });

    // Consultar schema de Users
    console.log('\nUsers schema:');
    const usersQuery = `
      SELECT column_name, data_type
      FROM information_schema.columns
      WHERE table_name = 'Users'
      ORDER BY ordinal_position;
    `;
    
    const usersResult = await sequelize.query(usersQuery, { type: 'SELECT' });
    usersResult.forEach(row => {
      console.log(`  ${row.column_name}: ${row.data_type}`);
    });

    process.exit(0);
  } catch (err) {
    console.error('ERROR:', err.message);
    process.exit(1);
  } finally {
    await sequelize.close();
  }
}

checkSchema();
