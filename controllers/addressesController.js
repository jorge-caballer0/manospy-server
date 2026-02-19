import { Address } from '../models/index.js';

// Crear nueva dirección
export const createAddress = async (req, res) => {
  try {
    const user_id = req.user.id; // Del JWT (UUID)
    const { direccion, ciudad, estado, codigo_postal } = req.body;
    
    console.log('createAddress: user_id =', user_id, 'type:', typeof user_id);
    console.log('createAddress: req.body =', req.body);

    if (!direccion || !ciudad) {
      return res.status(400).json({ error: 'direccion y ciudad son requeridos' });
    }

    const address = await Address.create({
      user_id,
      direccion,
      ciudad,
      estado: estado || null,
      codigo_postal: codigo_postal || null
    });
    console.log('createAddress: Address created =', address.id);

    res.status(201).json({ 
      message: 'Dirección agregada exitosamente',
      address
    });
  } catch (err) {
    console.error('Error en createAddress:', err.message, err.stack);
    res.status(500).json({ error: 'Error creando dirección', details: err.message });
  }
};

// Obtener todas las direcciones del usuario
export const getAddresses = async (req, res) => {
  try {
    const user_id = req.user.id; // Del JWT (UUID)
    console.log('getAddresses: user_id =', user_id);

    const addresses = await Address.findAll({
      where: { user_id }
    });
    console.log('getAddresses: found addresses =', addresses.length);

    res.json(addresses);
  } catch (err) {
    console.error('Error en getAddresses:', err.message, err.stack);
    res.status(500).json({ error: 'Error cargando direcciones', details: err.message });
  }
};

// Actualizar dirección
export const updateAddress = async (req, res) => {
  try {
    const { id } = req.params;
    const user_id = req.user.id; // Del JWT (UUID)
    const { direccion, ciudad, estado, codigo_postal } = req.body;

    const address = await Address.findByPk(id);
    if (!address) {
      return res.status(404).json({ error: 'Dirección no encontrada' });
    }

    if (address.user_id !== user_id) {
      return res.status(403).json({ error: 'No tienes permiso para editar esta dirección' });
    }

    await address.update({
      direccion: direccion || address.direccion,
      ciudad: ciudad || address.ciudad,
      estado: estado !== undefined ? estado : address.estado,
      codigo_postal: codigo_postal !== undefined ? codigo_postal : address.codigo_postal
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
    const user_id = req.user.id; // Del JWT (UUID)

    const address = await Address.findByPk(id);
    if (!address) {
      return res.status(404).json({ error: 'Dirección no encontrada' });
    }

    if (address.user_id !== user_id) {
      return res.status(403).json({ error: 'No tienes permiso para eliminar esta dirección' });
    }

    await address.destroy();

    res.json({ message: 'Dirección eliminada exitosamente' });
  } catch (err) {
    console.error('Error en deleteAddress:', err);
    res.status(500).json({ error: err.message });
  }
};
