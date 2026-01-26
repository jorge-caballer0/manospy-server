const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const { getPendingRequests, createRequest, getServiceById, acceptService } = require('../controllers/serviceController');

// ✅ CORRECCIÓN 7: Agregar rutas de servicios con autenticación
router.get('/requests', authMiddleware, getPendingRequests);        // Obtener solicitudes pendientes
router.post('/requests', authMiddleware, createRequest);             // Crear nueva solicitud
router.get('/requests/:id', authMiddleware, getServiceById);        // Obtener detalle de solicitud
router.post('/requests/:id/accept', authMiddleware, acceptService); // Aceptar solicitud de servicio

module.exports = router;
