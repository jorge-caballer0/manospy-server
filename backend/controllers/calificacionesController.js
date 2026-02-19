import * as calificacionesService from '../services/calificaciones.js';

export const rateSupport = async (req, res) => {
  try {
    const { user_id, agent_id, rating, tags, comment } = req.body;
    const { error } = await calificacionesService.rateSupport({ user_id, agent_id, rating, tags, comment });
    if (error) return res.status(400).json({ error: error.message });
    res.status(201).json({ message: 'Calificación de soporte registrada' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const rateUser = async (req, res) => {
  try {
    const { from_user_id, to_user_id, rating, tags, comment } = req.body;
    const { error } = await calificacionesService.rateUser({ from_user_id, to_user_id, rating, tags, comment });
    if (error) return res.status(400).json({ error: error.message });
    res.status(201).json({ message: 'Calificación registrada' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
