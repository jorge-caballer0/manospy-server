require('dotenv').config();
const { User, ServiceRequest, sequelize } = require('./models/index.js');
const bcrypt = require('bcryptjs');

async function run() {
  try {
    await sequelize.authenticate();
    console.log('✓ DB connected');

    // Crear profesional de prueba
    const professionalEmail = `prof_test_${Date.now()}@example.com`;
    const passwordHash = await bcrypt.hash('Password123', 10);
    
    const professional = await User.create({
      name: 'Juan Plomero - Testing',
      email: professionalEmail,
      password: passwordHash,
      role: 'professional',
      status: 'active',
      phoneNumber: '+34666777888',
      idNumber: '12345678X',
      category: 'Plomería',
      bio: 'Profesional de plomería con 10 años de experiencia',
      experience: 'Reparación de tuberías, instalación, mantenimiento',
      location: 'Buenos Aires',
      services: JSON.stringify(['Reparación', 'Instalación', 'Mantenimiento']),
      cities: JSON.stringify(['Buenos Aires', 'La Plata'])
    });

    console.log('✓ Profesional creado:');
    console.log(`  Email: ${professionalEmail}`);
    console.log(`  ID: ${professional.id}`);
    console.log(`  Nombre: ${professional.name}`);

    // Crear una oferta de prueba para ese profesional
    const clientUser = await User.findOne({ where: { email: 'cliente@example.com' } });
    const clientId = clientUser ? clientUser.id : professional.id; // Fallback si no existe

    const serviceRequest = await ServiceRequest.create({
      clientId: clientId,
      description: 'Necesito reparar una tubería rota en el baño',
      category: 'Plomería',
      status: 'pending',
      location: 'Calle Principal 123, Buenos Aires',
      preferredDate: '2026-02-25'
    });

    console.log('✓ Oferta (ServiceRequest) creada:');
    console.log(`  ID: ${serviceRequest.id}`);
    console.log(`  Categoría: ${serviceRequest.category}`);
    console.log(`  Descripción: ${serviceRequest.description}`);

    // Simular que la oferta viene como "offer_" + serviceRequestId (patrón usado en la app)
    const offerId = `offer_${serviceRequest.id}`;
    
    console.log('\n📋 DATOS PARA TESTING EN LA APP:');
    console.log('================================');
    console.log(`Profesional Email: ${professionalEmail}`);
    console.log(`Profesional ID: ${professional.id}`);
    console.log(`Oferta ID (para navegar): ${offerId}`);
    console.log(`ServiceRequest ID: ${serviceRequest.id}`);
    console.log('\nPasos para testear:');
    console.log('1. Busca la oferta por categoría "Plomería" o ubicación "Buenos Aires"');
    console.log('2. Abre la oferta y presiona "Escribir al Profesional"');
    console.log('3. Intenta enviar un mensaje');

    process.exit(0);
  } catch (err) {
    console.error('ERROR:', err.message);
    process.exit(1);
  } finally {
    await sequelize.close();
  }
}

run();
