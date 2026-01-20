const express = require('express');
const router = express.Router();
const { adminLogin, getStats, getPendingPros, approveProfessional, rejectProfessional, getUsers, blockUser } = require('../controllers/adminController');

// Rutas de administración
router.post('/login', adminLogin);
router.get('/stats', getStats);
router.get('/professionals/pending', getPendingPros);
router.post('/professionals/:id/approve', approveProfessional);
router.post('/professionals/:id/reject', rejectProfessional);
router.get('/users', getUsers);
router.post('/users/:id/block', blockUser);

module.exports = router;
