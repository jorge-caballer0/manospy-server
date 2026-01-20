const express = require('express');
const router = express.Router();
const { getPendingRequests, createRequest } = require('../controllers/serviceController');

// Rutas de servicios
router.get('/requests', getPendingRequests);
router.post('/requests', createRequest);

module.exports = router;
