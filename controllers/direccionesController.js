import * as direccionesService from '../services/direcciones.js';

export const addDireccion = async (req, res) => {
  try {
    const { user_id, direccion, ciudad, estado, codigo_postal } = req.body;
    const { error } = await direccionesService.addDireccion({ user_id, direccion, ciudad, estado, codigo_postal });
    if (error) return res.status(400).json({ error: error.message });
    res.status(201).json({ message: 'Dirección agregada' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const getDirecciones = async (req, res) => {
  try {
    const { user_id } = req.params;
    const { data, error } = await direccionesService.getDirecciones(user_id);
    if (error) return res.status(400).json({ error: error.message });
    res.json(data);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const updateDireccion = async (req, res) => {
  try {
    const { id } = req.params;
    const data = req.body;
    const { error } = await direccionesService.updateDireccion(id, data);
    if (error) return res.status(400).json({ error: error.message });
    res.json({ message: 'Dirección actualizada' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

export const deleteDireccion = async (req, res) => {
  try {
    const { id } = req.params;
    const { error } = await direccionesService.deleteDireccion(id);
    if (error) return res.status(400).json({ error: error.message });
    res.json({ message: 'Dirección eliminada' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
