import express from 'express';
import authMiddleware from '../middleware/authMiddleware.js';
import {
  createAddress,
  getAddresses,
  updateAddress,
  deleteAddress
} from '../controllers/addressesController.js';

const router = express.Router();

// Todas las rutas requieren autenticación
router.post('/', authMiddleware, createAddress);
router.get('/', authMiddleware, getAddresses);
router.put('/:id', authMiddleware, updateAddress);
router.delete('/:id', authMiddleware, deleteAddress);

export default router;
