const express = require('express');
const router = express.Router();
const { registerClient, registerProfessional, login } = require('../controllers/authController');

// Registro
router.post('/register/client', registerClient);
router.post('/register/professional', registerProfessional);

// Login
router.post('/login', login);

module.exports = router;
