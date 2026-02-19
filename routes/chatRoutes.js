import express from 'express';
import authMiddleware from '../middleware/authMiddleware.js';
import chatController from '../controllers/chatController.js';

const router = express.Router();

router.post('/', authMiddleware, chatController.createChat);
router.get('/:chatId/messages', authMiddleware, chatController.getChatMessages);
router.post('/:chatId/messages', authMiddleware, chatController.postChatMessage);
router.post('/:chatId/convert', authMiddleware, chatController.convertChatToReservation);

export default router;

