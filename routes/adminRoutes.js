const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const adminMiddleware = require('../middleware/adminMiddleware');

// Importar funciones del controlador
const {
  adminLogin,
  getStats,
  getPendingPros,
  approveProfessional,
  rejectProfessional,
  getUsers,
  blockUser
} = require('../controllers/adminController');

// ✅ CORRECCIÓN 11: Agregar middlewares de autenticación y autorización admin

// Login de administrador (sin autenticación, para obtener token)
router.post('/login', adminLogin);

// Resto de rutas requieren autenticación y rol admin
// Estadísticas generales
router.get('/stats', authMiddleware, adminMiddleware, getStats);

// Profesionales pendientes de validación
router.get('/professionals/pending', authMiddleware, adminMiddleware, getPendingPros);

// Aprobar profesional (requiere ID numérico en la URL)
router.post('/professionals/:id/approve', authMiddleware, adminMiddleware, approveProfessional);

// Rechazar profesional (requiere ID numérico en la URL)
router.post('/professionals/:id/reject', authMiddleware, adminMiddleware, rejectProfessional);

// Listar todos los usuarios (excepto admin)
router.get('/users', authMiddleware, adminMiddleware, getUsers);

// Bloquear usuario (requiere ID numérico en la URL)
router.post('/users/:id/block', authMiddleware, adminMiddleware, blockUser);

module.exports = router;
