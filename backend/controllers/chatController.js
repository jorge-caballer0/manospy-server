import { Chat, Message, ServiceRequest, Reservation, User } from '../models/index.js';
import { Op } from 'sequelize';

// Crear un nuevo chat (devuelve chatId)
export async function createChat(req, res) {
  try {
    const { offerId, professionalId } = req.body;
    console.log('createChat: req.body =', req.body);
    console.log('createChat: req.user =', req.user);
    
    // chatId único: usar timestamp + user id como prefijo si existe
    const userId = req.user ? req.user.id : null;
    const prefix = userId ? String(userId) : 'anon';
    const chatId = `chat_${prefix}_${Date.now()}`;
    
    console.log('createChat: Creating chat with chatId =', chatId, 'clientId =', userId, 'professionalId =', professionalId);
    
    const chat = await Chat.create({ 
      id: chatId
    });
    
    console.log('createChat: Chat created successfully:', chat.id);
    return res.json({ chatId: chat.id });
  } catch (err) {
    console.error('createChat error:', err.message, err.stack);
    return res.status(500).json({ message: 'Error creando chat', error: err.message });
  }
}

export async function getChatMessages(req, res) {
  try {
    const { chatId } = req.params;
    if (!chatId) return res.status(400).json({ message: 'chatId es requerido' });
    const messages = await Message.findAll({ where: { chatId }, order: [['timestamp', 'ASC']] });
    return res.json(messages);
  } catch (err) {
    console.error('getChatMessages error', err);
    return res.status(500).json({ message: 'Error cargando mensajes' });
  }
}

export async function postChatMessage(req, res) {
  try {
    const { chatId } = req.params;
    const { content } = req.body;
    if (!chatId) return res.status(400).json({ message: 'chatId es requerido' });
    if (!content || content.trim().length === 0) return res.status(400).json({ message: 'content es requerido' });
    
    const senderId = req.user ? String(req.user.id) : 'anonymous';
    const timestamp = Date.now();
    
    console.log('postChatMessage: Creating message - chatId:', chatId, 'senderId:', senderId, 'content:', content);

    const msg = await Message.create({ chatId, content, senderId, timestamp });
    console.log('postChatMessage: Message created successfully:', msg.id);
    return res.json(msg);
  } catch (err) {
    console.error('postChatMessage error', err);
    return res.status(500).json({ message: 'Error creando mensaje' });
  }
}

// Convertir chat en solicitud/reserva formal
export async function convertChatToReservation(req, res) {
  try {
    const { chatId } = req.params;
    const chat = await Chat.findByPk(chatId);
    if (!chat) return res.status(404).json({ message: 'Chat no encontrado' });

    // Validar que haya al menos un mensaje en el chat
    const count = await Message.count({ where: { chatId } });
    if (count === 0) return res.status(400).json({ message: 'No hay mensajes en el chat para convertir' });

    // El cliente logueado es quien convierte el chat
    const clientId = req.user ? req.user.id : null;
    if (!clientId) return res.status(401).json({ message: 'Usuario no autenticado' });

    // Crear ServiceRequest y Reservation con estado EN_PROCESO
    const serviceRequest = await ServiceRequest.create({
      clientId: clientId,
      description: `Solicitud convertida desde chat ${chatId}`,
      category: 'general',
      status: 'EN_PROCESO'
    });

    const reservation = await Reservation.create({
      clientId: clientId,
      professionalId: null, // Se asigna cuando el profesional acepta
      serviceRequestId: serviceRequest.id,
      status: 'EN_PROCESO'
    });

    // Asociar mensajes del chat a la nueva reserva (set reservationId)
    await Message.update({ reservationId: reservation.id }, { where: { chatId } });

    console.log('convertChatToReservation: Converted chat', chatId, 'to reservation', reservation.id);
    return res.json({ reservationId: reservation.id, serviceRequestId: serviceRequest.id });
  } catch (err) {
    console.error('convertChatToReservation error', err);
    return res.status(500).json({ message: 'Error convirtiendo chat' });
  }
}

export default {
  createChat,
  getChatMessages,
  postChatMessage,
  convertChatToReservation
};

