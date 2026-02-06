import express from 'express';
import { sendSupportMessage, getSupportMessages } from '../controllers/soporteController.js';

const router = express.Router();

router.post('/send', sendSupportMessage);
router.get('/:chatId', getSupportMessages);

export default router;
