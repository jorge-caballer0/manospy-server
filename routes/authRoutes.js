import express from 'express';
import { registerClient, registerProfessional, login, getCurrentUser } from '../controllers/authController.js';
import authMiddleware from '../middleware/authMiddleware.js';
// Phone verification controller (CommonJS module exported)
const { requestPhoneVerification, verifyPhoneWithCode } = require('../controllers/phoneController');

const router = express.Router();

// Registro
router.post('/register/client', registerClient);
router.post('/register/professional', registerProfessional);

// Login
router.post('/login', login);

// ✅ Nuevo endpoint protegido
router.get('/me', authMiddleware, getCurrentUser);

// Solicitar código OTP (protegido)
router.post('/phone/verify-request', authMiddleware, (req, res) => requestPhoneVerification(req, res));
// Verificar código y cambiar teléfono
router.post('/phone/verify', (req, res) => verifyPhoneWithCode(req, res));

export default router;
