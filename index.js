// index.js
import express from 'express';
import cors from 'cors';
import path from 'path';
import dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname } from 'path';

dotenv.config();

// Importar sequelize después de cargar variables de entorno
const { sequelize } = await import('./models/index.js'); // conexión y modelos

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const app = express();

// Middlewares
app.use(express.json({ limit: '50mb' })); // Aumentar límite para base64
app.use(express.urlencoded({ limit: '50mb', extended: true }));
app.use(cors());

// Servir archivos estáticos
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Importar rutas (todas deben ser default export)
import authRoutes from './routes/authRoutes.js';
import adminRoutes from './routes/adminRoutes.js';
import serviceRoutes from './routes/serviceRoutes.js';
import reservationRoutes from './routes/reservationRoutes.js';
import uploadRoutes from './routes/uploadRoutes.js';
import soporteRoutes from './routes/soporteRoutes.js';
import calificacionesRoutes from './routes/calificacionesRoutes.js';
import direccionesRoutes from './routes/direccionesRoutes.js';

// Usar rutas
app.use('/auth', authRoutes);
app.use('/admin', adminRoutes);
app.use('/services', serviceRoutes);
app.use('/reservations', reservationRoutes);
app.use('/upload', uploadRoutes);
app.use('/soporte', soporteRoutes);
app.use('/calificaciones', calificacionesRoutes);
app.use('/direcciones', direccionesRoutes);

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


