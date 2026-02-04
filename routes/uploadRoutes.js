const express = require('express');
const router = express.Router();
const { uploadPhotoBase64, getPhoto } = require('../controllers/uploadController');

// Endpoint para subir foto en base64
router.post('/photo', uploadPhotoBase64);

// Endpoint para obtener foto
router.get('/photo/:fileName', getPhoto);

module.exports = router;
