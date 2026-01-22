const express = require('express');
const router = express.Router();
const { registerClient, registerProfessional, login, getCurrentUser } = require('../controllers/authController');
const authMiddleware = require('../middleware/authMiddleware'); // asegúrate de tener este archivo

// Registro
router.post('/register/client', registerClient);
router.post('/register/professional', registerProfessional);

// Login
router.post('/login', login);

// ✅ Nuevo endpoint protegido
router.get('/me', authMiddleware, getCurrentUser);

module.exports = router;
