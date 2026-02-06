import express from 'express';
import authMiddleware from '../middleware/authMiddleware.js';
import {
  getReservations,
  createReservation,
  acceptReservation,
  rejectReservation,
  getMessages,
  getReservationsByUser,
  getReservationById,
  addMessage,
  rateReservation
} from '../controllers/reservationController.js';

const router = express.Router();

// ✅ CORRECCIÓN 9: Rutas de reservas con autenticación
router.get('/', authMiddleware, getReservations);
router.get('/user', authMiddleware, getReservationsByUser);
router.post('/', authMiddleware, createReservation);
router.post('/:id/accept', authMiddleware, acceptReservation);
router.post('/:id/reject', authMiddleware, rejectReservation);
router.get('/:id/messages', authMiddleware, getMessages);
router.get('/:id', authMiddleware, getReservationById);
router.post('/:id/messages', authMiddleware, addMessage);
router.post('/:id/rate', authMiddleware, rateReservation);

export default router;
