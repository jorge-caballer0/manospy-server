import express from 'express';
import authMiddleware from '../middleware/authMiddleware.js';
import chatController from '../controllers/chatController.js';

const router = express.Router();

router.post('/', authMiddleware, chatController.createChat);
router.get('/', authMiddleware, chatController.listAllConversations); // Reemplaza listClientChats con versión unificada
router.get('/:chatId/messages', authMiddleware, chatController.getChatMessages);
router.post('/:chatId/messages', authMiddleware, chatController.postChatMessage);
router.put('/:messageId/read', authMiddleware, chatController.markMessageAsRead);
router.post('/:chatId/convert', authMiddleware, chatController.convertChatToReservation);

export default router;

