import express from 'express';
import authMiddleware from '../middleware/authMiddleware.js';
import offersController from '../controllers/offersController.js';

const router = express.Router();

// Obtener todas las ofertas (públicas - sin auth)
router.get('/', offersController.getAllOffers);

// Obtener detalle de una oferta (público - sin auth)
router.get('/:offerId', offersController.getOfferDetail);

// Crear nueva oferta (profesional - con auth)
router.post('/', authMiddleware, offersController.createOffer);

// Obtener mis ofertas (profesional - con auth)
router.get('/my-offers', authMiddleware, offersController.getMyOffers);

// Actualizar oferta (profesional - con auth)
router.put('/:offerId', authMiddleware, offersController.updateOffer);

// Eliminar oferta (profesional - con auth)
router.delete('/:offerId', authMiddleware, offersController.deleteOffer);

export default router;
