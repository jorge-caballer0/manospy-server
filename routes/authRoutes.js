import express from 'express';
import { registerClient, registerProfessional, login, getCurrentUser } from '../controllers/authController.js';
import authMiddleware from '../middleware/authMiddleware.js';
// Phone verification controller (CommonJS module exported)
import phoneController from '../controllers/phoneController.js';
const { requestPhoneVerification, verifyPhoneWithCode } = phoneController;
// Email verification controller
import emailController from '../controllers/emailController.js';
const { requestEmailVerification, verifyEmailCode, requestEmailChange, verifyEmailChange } = emailController;

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

// Email verification endpoints
router.post('/email/verify-request', authMiddleware, (req, res) => requestEmailVerification(req, res));
router.post('/email/verify', (req, res) => verifyEmailCode(req, res));
router.post('/email/change-request', authMiddleware, (req, res) => requestEmailChange(req, res));
router.post('/email/change-verify', (req, res) => verifyEmailChange(req, res));

export default router;
