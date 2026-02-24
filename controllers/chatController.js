import { Chat, Message, ServiceRequest, Reservation, User, Sequelize } from '../models/index.js';
import { Op } from 'sequelize';

// Crear un nuevo chat (devuelve chatId)
export async function createChat(req, res) {
  try {
    const { offerId, professionalId } = req.body;
    console.log('createChat: req.body =', req.body);
    console.log('createChat: req.user =', req.user);
    
    // chatId único: usar timestamp + user id como prefijo si existe
    const userId = req.user ? String(req.user.id) : null;
    const prefix = userId ? String(userId) : 'anon';
    const chatId = `chat_${prefix}_${Date.now()}`;
    
    console.log('createChat: Creating chat with chatId =', chatId, 'clientId =', userId, 'professionalId =', professionalId);
    
    const chat = await Chat.create({ 
      id: chatId,
      offerId: offerId || null,
      clientId: userId || null,
      professionalId: professionalId || null 
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

// Listar chats del cliente actual
export async function listClientChats(req, res) {
  try {
    const rawUserId = req.user ? req.user.id : null;
    if (!rawUserId) return res.status(401).json({ message: 'Usuario no autenticado' });

    const userIdStr = String(rawUserId);

    const chats = await Chat.findAll({
      where: Sequelize.where(Sequelize.col('client_id'), Op.eq, userIdStr),
      order: [['id', 'DESC']]  // No hay updatedAt en tabla chats
    });

    // Agregar último mensaje y datos del profesional para cada chat
    const chatsWithMessages = await Promise.all(
      chats.map(async (chat) => {
        const lastMessage = await Message.findOne({
          where: { chatId: chat.id },
          order: [['timestamp', 'DESC']]
        });

        // Buscar el profesional manualmente (sin usar include FK)
        let professional = null;
        if (chat.professionalId) {
          professional = await User.findOne({
            where: { id: parseInt(chat.professionalId) },
            attributes: ['id', 'name', 'profilePhotoUrl']
          });
        }

        return {
          id: chat.id,
          offerId: chat.offerId,
          clientId: chat.clientId,
          professionalId: chat.professionalId,
          professional: professional,
          lastMessage: lastMessage?.content || '',
          lastMessageTime: lastMessage?.timestamp || Date.now()
        };
      })
    );

    return res.json(chatsWithMessages);
  } catch (err) {
    console.error('listClientChats error', err);
    return res.status(500).json({ message: 'Error listando chats' });
  }
}

// Listar TODAS las conversaciones del cliente (chats pre-reserva + reservas formales)
export async function listAllConversations(req, res) {
  try {
    const rawUserId = req.user ? req.user.id : null;
    if (!rawUserId) return res.status(401).json({ message: 'Usuario no autenticado' });

    const userIdStr = String(rawUserId);

    // Obtener chats pre-reserva del cliente - usar operador de comparación simple
    const chatsPreReserva = await Chat.findAll({
      where: Sequelize.where(Sequelize.col('client_id'), Op.eq, userIdStr),
      order: [['id', 'DESC']]  // No hay updatedAt en tabla chats
    });

    // Para cada chat pre-reserva, obtener último mensaje y datos profesional
    const chatsWithMessages = await Promise.all(
      chatsPreReserva.map(async (chat) => {
        const lastMessage = await Message.findOne({
          where: { chatId: chat.id },
          order: [['timestamp', 'DESC']]
        });

        // Buscar el profesional manualmente
        let professional = null;
        if (chat.professionalId) {
          professional = await User.findOne({
            where: { id: parseInt(chat.professionalId) },
            attributes: ['id', 'name', 'profilePhotoUrl']
          });
        }

        return {
          id: chat.id,
          type: 'chat', // tipo: pre-reserva
          offerId: chat.offerId,
          reservationId: null,
          professional: professional,
          lastMessage: lastMessage?.content || '',
          lastMessageTime: lastMessage?.timestamp || Date.now()
        };
      })
    );

    // Obtener reservas del cliente (formales)
    // Para reservas usamos el id numérico porque la columna clientId es INTEGER
    const userIdNum = parseInt(rawUserId);
    const reservations = await Reservation.findAll({
      where: { clientId: userIdNum },
      include: [
        {
          model: User,
          as: 'professional',
          attributes: ['id', 'name', 'profilePhotoUrl']
        }
      ],
      order: [['updatedAt', 'DESC']]
    });

    // Para cada reserva, obtener último mensaje
    const reservationsWithMessages = await Promise.all(
      reservations.map(async (reservation) => {
        const lastMessage = await Message.findOne({
          where: { reservationId: reservation.id },
          order: [['timestamp', 'DESC']]
        });

        return {
          id: reservation.id,
          type: 'reservation', // tipo: reserva formal
          offerId: null,
          reservationId: reservation.id,
          professional: reservation.professional,
          lastMessage: lastMessage?.content || '',
          lastMessageTime: lastMessage?.timestamp || Date.now()
        };
      })
    );

    // Combinar y ordenar por última actualización (más reciente primero)
    const allConversations = [...chatsWithMessages, ...reservationsWithMessages].sort(
      (a, b) => (new Date(b.lastMessageTime) || 0) - (new Date(a.lastMessageTime) || 0)
    );

    return res.json(allConversations);
  } catch (err) {
    console.error('listAllConversations error', err);
    return res.status(500).json({ message: 'Error listando conversaciones' });
  }
}

// Marcar un mensaje como leído
export async function markMessageAsRead(req, res) {
  try {
    const { messageId } = req.params;
    const message = await Message.findByPk(messageId);
    
    if (!message) return res.status(404).json({ message: 'Mensaje no encontrado' });

    // Mensaje marcado (sin cambios en BD ya que no tiene readStatus)
    return res.json({ id: message.id, message: 'Marcado como leído' });
  } catch (err) {
    console.error('markMessageAsRead error', err);
    return res.status(500).json({ message: 'Error marcando mensaje como leído' });
  }
}

// Convertir chat en solicitud/reserva formal
export async function convertChatToReservation(req, res) {
  try {
    const { chatId } = req.params;
    console.log('convertChatToReservation: Attempting to convert chatId:', chatId);
    
    // Primero intentar buscar por chatId exacto
    let chat = await Chat.findByPk(chatId);
    
    // Si no existe, intentar buscar por offerId (para compatibilidad cuando se pasa offer.id)
    if (!chat) {
      console.log('convertChatToReservation: Chat exact not found, searching by offerId:', chatId);
      chat = await Chat.findOne({ where: { offerId: chatId } });
    }
    
    if (!chat) return res.status(404).json({ message: 'Chat no encontrado' });

    // Validar que haya al menos un mensaje en el chat
    const count = await Message.count({ where: { chatId: chat.id } });
    if (count === 0) return res.status(400).json({ message: 'No hay mensajes en el chat para convertir' });

    // El cliente logueado es quien convierte el chat
    const clientId = chat.clientId || (req.user ? String(req.user.id) : null);
    const professionalId = chat.professionalId || null;
    
    if (!clientId) return res.status(401).json({ message: 'Usuario no autenticado' });

    console.log('convertChatToReservation: Converting chat', chat.id, 'clientId:', clientId, 'professionalId:', professionalId);

    // Crear ServiceRequest y Reservation con estado EN_PROCESO
    const serviceRequest = await ServiceRequest.create({
      clientId: clientId,
      description: `Solicitud convertida desde chat ${chatId}`,
      category: 'general',
      status: 'EN_PROCESO'
    });

    const reservation = await Reservation.create({
      clientId: clientId,
      professionalId: professionalId,
      serviceRequestId: serviceRequest.id,
      status: 'EN_PROCESO'
    });

    // Asociar mensajes del chat a la nueva reserva (set reservationId)
    await Message.update({ reservationId: reservation.id }, { where: { chatId: chat.id } });

    console.log('convertChatToReservation: Converted chat', chat.id, 'to reservation', reservation.id);
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
  listClientChats,
  listAllConversations,
  markMessageAsRead,
  convertChatToReservation
};

