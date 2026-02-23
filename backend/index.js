// index.js
import express from 'express';
import cors from 'cors';
import path from 'path';
import dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import pg from 'pg';
import fs from 'fs';

dotenv.config();

const { Pool } = pg;

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
import addressesRoutes from './routes/addressesRoutes.js';
import chatRoutes from './routes/chatRoutes.js';
import offersRoutes from './routes/offersRoutes.js';

// Usar rutas
app.use('/auth', authRoutes);
app.use('/admin', adminRoutes);
app.use('/services', serviceRoutes);
app.use('/reservations', reservationRoutes);
app.use('/upload', uploadRoutes);
app.use('/soporte', soporteRoutes);
app.use('/calificaciones', calificacionesRoutes);
app.use('/direcciones', direccionesRoutes);
app.use('/addresses', addressesRoutes);
app.use('/chats', chatRoutes);
app.use('/offers', offersRoutes);

// Ruta base
app.get('/', (req, res) => {
  res.send('Servidor ManosPy funcionando 🚀');
});

// Función para ejecutar migraciones
async function runMigrations() {
  const pool = new Pool({
    connectionString: process.env.DATABASE_URL
  });
  const client = await pool.connect();
  
  try {
    console.log('🔄 Ejecutando migraciones...');
    
    const migrations = [
      './migrations/20260219_add_chats_and_chatid.sql',
      './migrations/20260223_fix_chats_client_id_type.sql'
    ];
    
    for (const migrationFile of migrations) {
      try {
        const migrationSQL = fs.readFileSync(migrationFile, 'utf8');
        console.log(`📋 Ejecutando: ${migrationFile}`);
        
        await client.query(migrationSQL);
        console.log(`✅ ${migrationFile} - Exitosa`);
      } catch (fileErr) {
        if (fileErr.code === 'ENOENT') {
          console.warn(`⚠️ ${migrationFile} - No encontrado, saltando...`);
        } else if (fileErr.code === '42P07') {
          console.warn(`⚠️ ${migrationFile} - Tabla ya existe, saltando...`);
        } else {
          throw fileErr;
        }
      }
    }
    
    console.log('✅ Todas las migraciones completadas');
  } catch (err) {
    console.error('❌ Error ejecutando migraciones:', err.message);
    // No detener el servidor si hay error en migration
  } finally {
    client.release();
    await pool.end();
  }
}

// Iniciar servidor
const PORT = process.env.PORT || 3000; // Render asigna PORT automáticamente

async function startServer() {
  try {
    // Ejecutar migraciones primero
    await runMigrations();
    
    // Luego sincronizar sequelize
    await sequelize.sync({ alter: false });
    console.log("✅ Base de datos sincronizada correctamente");
    
    // Finalmente iniciar express
    app.listen(PORT, () => {
      console.log(`🚀 Servidor corriendo en puerto ${PORT}`);
    });
  } catch (err) {
    console.error("❌ Error al iniciar servidor:", err);
    process.exit(1);
  }
}

startServer();


