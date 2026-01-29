const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const {
  getReservations,
  createReservation,
  acceptReservation,
  rejectReservation,
  getMessages,
  addMessage,
  rateReservation  // ✅ CORRECCIÓN 9
} = require('../controllers/reservationController');

// ✅ CORRECCIÓN 9: Rutas de reservas con autenticación
router.get('/', authMiddleware, getReservations);

// Obtener reservas por usuario (cliente)
router.get('/user/:id', authMiddleware, getReservationsByUser);

// Crear nueva reserva (cliente solicita servicio)
router.post('/', authMiddleware, createReservation);

// Profesional acepta/rechaza reserva
router.post('/:id/accept', authMiddleware, acceptReservation);
router.post('/:id/reject', authMiddleware, rejectReservation);

// Chat de mensajes dentro de una reserva
router.get('/:id/messages', authMiddleware, getMessages);
// Obtener detalle de reserva por id
router.get('/:id', authMiddleware, getReservationById);
router.post('/:id/messages', authMiddleware, addMessage);

// ✅ CORRECCIÓN 9: Calificar/Revisar una reserva completada
router.post('/:id/rate', authMiddleware, rateReservation);

module.exports = router;
