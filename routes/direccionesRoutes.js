import express from 'express';
import { addDireccion, getDirecciones, updateDireccion, deleteDireccion } from '../controllers/direccionesController.js';

const router = express.Router();

router.post('/', addDireccion);
router.get('/:user_id', getDirecciones);
router.put('/:id', updateDireccion);
router.delete('/:id', deleteDireccion);

export default router;
