import * as soporteService from '../services/soporte.js';

export const sendSupportMessage = async (req, res) => {
  try {
    const { user_id, agent_id, message, type } = req.body;
    const { error } = await soporteService.sendSupportMessage({ user_id, agent_id, message, type });
    if (error) return res.status(400).json({ error: error.message });
    res.status(201).json({ message: 'Mensaje enviado' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const getSupportMessages = async (req, res) => {
  try {
    const { chatId } = req.params;
    const { data, error } = await soporteService.getSupportMessages(chatId);
    if (error) return res.status(400).json({ error: error.message });
    res.json(data);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
