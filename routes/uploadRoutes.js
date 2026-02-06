import express from 'express';
import { uploadPhotoBase64, getPhoto } from '../controllers/uploadController.js';

const router = express.Router();

// Endpoint para subir foto en base64
router.post('/photo', uploadPhotoBase64);

// Endpoint para obtener foto
router.get('/photo/:fileName', getPhoto);

export default router;
