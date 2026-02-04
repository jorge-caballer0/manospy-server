// index.js
const express = require('express');
const cors = require('cors');
const path = require('path');
require('dotenv').config();
const { sequelize } = require('./models'); // conexión y modelos

const app = express();

// Middlewares
app.use(express.json({ limit: '50mb' })); // Aumentar límite para base64
app.use(express.urlencoded({ limit: '50mb', extended: true }));
app.use(cors());

// Servir archivos estáticos
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Importar rutas
const authRoutes = require('./routes/authRoutes');
const adminRoutes = require('./routes/adminRoutes');
const serviceRoutes = require('./routes/serviceRoutes');
const reservationRoutes = require('./routes/reservationRoutes');
const uploadRoutes = require('./routes/uploadRoutes');

// Usar rutas
app.use('/auth', authRoutes);
app.use('/admin', adminRoutes);
app.use('/services', serviceRoutes);
app.use('/reservations', reservationRoutes);
app.use('/upload', uploadRoutes);

// Ruta base
app.get('/', (req, res) => {
  res.send('Servidor ManosPy funcionando 🚀');
});

// Iniciar servidor
const PORT = process.env.PORT || 3000; // Render asigna PORT automáticamente

sequelize
  .sync({ alter: true }) // ajusta tablas sin borrar datos; usar { force: true } solo en desarrollo
  .then(() => {
    console.log("✅ Base de datos sincronizada correctamente");
    app.listen(PORT, () => {
      console.log(`🚀 Servidor corriendo en puerto ${PORT}`);
    });
  })
  .catch(err => {
    console.error("❌ Error al sincronizar base de datos:", err);
  });


