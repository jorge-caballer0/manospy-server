import { Address, User, Op } from '../models/index.js';

// Crear nueva dirección
export const createAddress = async (req, res) => {
  try {
    // El JWT puede contener ID integer o UUID, buscar usuario en BD para obtener UUID correcto
    const jwtId = req.user.id; 
    const { direccion, ciudad, estado, codigo_postal } = req.body;
    
    console.log('createAddress: jwtId =', jwtId, 'type:', typeof jwtId);
    console.log('createAddress: req.body =', req.body);

    if (!direccion || !ciudad) {
      return res.status(400).json({ error: 'direccion y ciudad son requeridos' });
    }

    // Buscar usuario para obtener su UUID correcto (en caso que JWT tenga ID integer)
    const user = await User.findOne({
      where: { [Op.or]: [ { id: jwtId }, { id: String(jwtId) } ] }
    });

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    const user_id = user.id; // UUID del usuario desde la BD
    console.log('createAddress: user found with id =', user_id);

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
    const jwtId = req.user.id;
    console.log('getAddresses: jwtId =', jwtId);

    // Buscar usuario para obtener su UUID correcto
    const user = await User.findOne({
      where: { [Op.or]: [ { id: jwtId }, { id: String(jwtId) } ] }
    });

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    const user_id = user.id;
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
    const jwtId = req.user.id;
    const { direccion, ciudad, estado, codigo_postal } = req.body;

    // Buscar usuario para obtener su UUID correcto
    const user = await User.findOne({
      where: { [Op.or]: [ { id: jwtId }, { id: String(jwtId) } ] }
    });

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    const user_id = user.id;

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
    const jwtId = req.user.id;

    // Buscar usuario para obtener su UUID correcto
    const user = await User.findOne({
      where: { [Op.or]: [ { id: jwtId }, { id: String(jwtId) } ] }
    });

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    const user_id = user.id;

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
