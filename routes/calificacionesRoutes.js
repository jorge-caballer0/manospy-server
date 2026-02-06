import express from 'express';
import { rateSupport, rateUser } from '../controllers/calificacionesController.js';

const router = express.Router();

router.post('/soporte', rateSupport);
router.post('/usuario', rateUser);

export default router;
