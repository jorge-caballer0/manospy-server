const express = require('express');
const router = express.Router();
const { getReservations, getMessages, addMessage } = require('../controllers/reservationController');

// Rutas de reservas
router.get('/', getReservations);
router.get('/:id/messages', getMessages);
router.post('/:id/messages', addMessage);

module.exports = router;
