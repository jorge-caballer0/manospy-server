const express = require('express');
const router = express.Router();

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

// Rutas de administración

// Login de administrador
router.post('/login', adminLogin);

// Estadísticas generales
router.get('/stats', getStats);

// Profesionales pendientes de validación
router.get('/professionals/pending', getPendingPros);

// Aprobar profesional (requiere ID numérico en la URL)
router.post('/professionals/:id/approve', approveProfessional);

// Rechazar profesional (requiere ID numérico en la URL)
router.post('/professionals/:id/reject', rejectProfessional);

// Listar todos los usuarios (excepto admin)
router.get('/users', getUsers);

// Bloquear usuario (requiere ID numérico en la URL)
router.post('/users/:id/block', blockUser);

module.exports = router;
