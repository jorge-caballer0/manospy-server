const express = require('express');
const router = express.Router();
const {
  getReservations,
  createReservation,
  acceptReservation,
  rejectReservation,
  getMessages,
  addMessage
} = require('../controllers/reservationController');

// Rutas de reservas
router.get('/', getReservations);

// Crear nueva reserva (cliente solicita servicio)
router.post('/', createReservation);

// Profesional acepta/rechaza reserva
router.post('/:id/accept', acceptReservation);
router.post('/:id/reject', rejectReservation);

// Chat de mensajes dentro de una reserva
router.get('/:id/messages', getMessages);
router.post('/:id/messages', addMessage);

module.exports = router;
