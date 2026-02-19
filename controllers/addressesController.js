import { Address } from '../models/index.js';

// Crear nueva dirección
export const createAddress = async (req, res) => {
  try {
    const userId = req.user.id; // Del JWT
    const { fullAddress, label, latitude, longitude } = req.body;

    if (!fullAddress || !label) {
      return res.status(400).json({ error: 'fullAddress y label son requeridos' });
    }

    const address = await Address.create({
      userId,
      fullAddress,
      label,
      latitude: latitude || null,
      longitude: longitude || null
    });

    res.status(201).json({ 
      message: 'Dirección agregada exitosamente',
      address
    });
  } catch (err) {
    console.error('Error en createAddress:', err);
    res.status(500).json({ error: err.message });
  }
};

// Obtener todas las direcciones del usuario
export const getAddresses = async (req, res) => {
  try {
    const userId = req.user.id; // Del JWT

    const addresses = await Address.findAll({
      where: { userId },
      order: [['createdAt', 'DESC']]
    });

    res.json(addresses);
  } catch (err) {
    console.error('Error en getAddresses:', err);
    res.status(500).json({ error: err.message });
  }
};

// Actualizar dirección
export const updateAddress = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.user.id;
    const { fullAddress, label, latitude, longitude } = req.body;

    const address = await Address.findByPk(id);
    if (!address) {
      return res.status(404).json({ error: 'Dirección no encontrada' });
    }

    if (address.userId !== userId) {
      return res.status(403).json({ error: 'No tienes permiso para editar esta dirección' });
    }

    await address.update({
      fullAddress: fullAddress || address.fullAddress,
      label: label || address.label,
      latitude: latitude !== undefined ? latitude : address.latitude,
      longitude: longitude !== undefined ? longitude : address.longitude
    });

    res.json({ 
      message: 'Dirección actualizada exitosamente',
      address
    });
  } catch (err) {
    console.error('Error en updateAddress:', err);
    res.status(500).json({ error: err.message });
  }
};

// Eliminar dirección
export const deleteAddress = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.user.id;

    const address = await Address.findByPk(id);
    if (!address) {
      return res.status(404).json({ error: 'Dirección no encontrada' });
    }

    if (address.userId !== userId) {
      return res.status(403).json({ error: 'No tienes permiso para eliminar esta dirección' });
    }

    await address.destroy();

    res.json({ message: 'Dirección eliminada exitosamente' });
  } catch (err) {
    console.error('Error en deleteAddress:', err);
    res.status(500).json({ error: err.message });
  }
};
