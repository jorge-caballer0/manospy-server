import jwt from 'jsonwebtoken';
import dotenv from 'dotenv';

dotenv.config();

const API_BASE_URL = 'http://localhost:3000';
const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key-from-env';

// Generate JWT token for professional user (ID 22)
function generateJWT(userId) {
  const token = jwt.sign(
    { id: userId },
    JWT_SECRET,
    { expiresIn: '24h' }
  );
  return token;
}

// Create a real offer for professional ID 22
async function createRealOffer() {
  try {
    const professionalId = 22;
    const jwtToken = generateJWT(professionalId);

    console.log('🔑 JWT Token generated for professional ID:', professionalId);
    console.log('Token:', jwtToken);
    console.log('');

    const offerData = {
      title: 'Servicios de Plomería Profesional',
      description: 'Reparación de tuberías, instalación de sanitarios, mantenimiento de sistemas de agua. Experiencia de 10+ años, trabajos garantizados.',
      category: 'Plumbing',
      location: 'Buenos Aires, Argentina',
      price: 500,
      currency: 'ARS',
      availability: JSON.stringify({
        monday: { start: '08:00', end: '18:00' },
        tuesday: { start: '08:00', end: '18:00' },
        wednesday: { start: '08:00', end: '18:00' },
        thursday: { start: '08:00', end: '18:00' },
        friday: { start: '08:00', end: '18:00' },
        saturday: { start: '09:00', end: '14:00' }
      }),
      status: 'active'
    };

    console.log('📋 Creating offer with following data:');
    console.log(JSON.stringify(offerData, null, 2));
    console.log('');

    const response = await fetch(`${API_BASE_URL}/offers`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${jwtToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(offerData)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw { 
        status: response.status, 
        data: errorData 
      };
    }

    const responseData = await response.json();

    console.log('✅ Offer created successfully!');
    console.log('');
    console.log('📊 Offer Details:');
    console.log('  - ID:', responseData.id);
    console.log('  - Title:', responseData.title);
    console.log('  - Professional ID:', responseData.professionalId);
    console.log('  - Price:', responseData.price, responseData.currency);
    console.log('  - Status:', responseData.status);
    console.log('  - Location:', responseData.location);
    console.log('');
    console.log('Full response:', JSON.stringify(responseData, null, 2));

    // Now provide instructions for testing
    console.log('');
    console.log('🧪 Testing Instructions:');
    console.log('1. In the mobile app, log in as a CLIENT user');
    console.log('2. Go to Home screen - you should see the new offer from this professional');
    console.log('3. Click on the offer to view details');
    console.log('4. Click "Escribir al Profesional" or "Iniciar Chat" button');
    console.log('5. Verify chat window opens and you can send a message');
    console.log('6. Message should be saved and you should see it in real-time');
    console.log('');

    return responseData;
  } catch (error) {
    console.error('❌ Error creating offer:');
    if (error.status) {
      console.error('Status:', error.status);
      console.error('Data:', JSON.stringify(error.data, null, 2));
    } else {
      console.error('Message:', error.message);
    }
    throw error;
  }
}

createRealOffer();
