import express from 'express';
import authMiddleware from '../middleware/authMiddleware.js';
import { getPendingRequests, createRequest, getServiceById, acceptService, cancelService } from '../controllers/serviceController.js';

const router = express.Router();

// ✅ CORRECCIÓN 7: Agregar rutas de servicios con autenticación
router.get('/requests', authMiddleware, getPendingRequests);        // Obtener solicitudes pendientes
router.post('/requests', authMiddleware, createRequest);             // Crear nueva solicitud
router.get('/requests/:id', authMiddleware, getServiceById);        // Obtener detalle de solicitud
router.post('/requests/:id/accept', authMiddleware, acceptService); // Aceptar solicitud de servicio
router.delete('/requests/:id', authMiddleware, cancelService);      // Cancelar solicitud de servicio

export default router;
