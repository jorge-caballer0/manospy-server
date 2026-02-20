require('dotenv').config();
const { Message, Chat, sequelize } = require('./models/index.js');

async function run() {
  try {
    await sequelize.authenticate();
    console.log('DB connected');

    const chatId = 'offer1';
    const content = 'Prueba desde script';
    const senderId = '10';
    const timestamp = Date.now();

    console.log('Creating message...', { chatId, content, senderId, timestamp });
    const msg = await Message.create({ chatId, content, senderId, timestamp });
    console.log('Created message:', msg.toJSON());
  } catch (err) {
    console.error('ERROR creating message:', err);
    if (err.original) console.error('Original:', err.original);
    process.exit(1);
  } finally {
    await sequelize.close();
  }
}

run();
