import express from 'express';
import { registerClient, registerProfessional, login, getCurrentUser } from '../controllers/authController.js';
import authMiddleware from '../middleware/authMiddleware.js';

const router = express.Router();

// Registro
router.post('/register/client', registerClient);
router.post('/register/professional', registerProfessional);

// Login
router.post('/login', login);

// ✅ Nuevo endpoint protegido
router.get('/me', authMiddleware, getCurrentUser);

export default router;
