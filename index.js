// index.js
const express = require('express');
const cors = require('cors');
require('dotenv').config();
const { sequelize } = require('./models'); // importamos conexión y modelos

const app = express();
app.use(express.json());
app.use(cors());

// Importar rutas
const authRoutes = require('./routes/authRoutes');
const adminRoutes = require('./routes/adminRoutes');
const serviceRoutes = require('./routes/serviceRoutes');
const reservationRoutes = require('./routes/reservationRoutes');

// Usar rutas
app.use('/auth', authRoutes);
app.use('/admin', adminRoutes);
app.use('/services', serviceRoutes);
app.use('/reservations', reservationRoutes);

// Ruta base
app.get('/', (req, res) => {
  res.send('Servidor ManosPy funcionando 🚀');
});

// Iniciar servidor
const PORT = process.env.PORT || 10000;
sequelize.sync({ alter: true }).then(async () => {
  console.log("Base de datos sincronizada");
  app.listen(PORT, () => console.log(`Servidor corriendo en puerto ${PORT}`));
}).catch(err => {
  console.error("Error al sincronizar base de datos:", err);
});
