import { ProfessionalOffer, User } from '../models/index.js';

// Crear nueva oferta (profesional)
export async function createOffer(req, res) {
  try {
    const professionalId = req.user ? req.user.id : null;
    if (!professionalId) {
      return res.status(401).json({ message: 'No autorizado' });
    }

    const { title, description, category, location, price, currency, availability, image_url } = req.body;

    if (!title || !description || !category) {
      return res.status(400).json({ message: 'Campos requeridos: title, description, category' });
    }

    const offer = await ProfessionalOffer.create({
      professionalId,
      title,
      description,
      category,
      location,
      price,
      currency,
      availability,
      image_url,
      status: 'active'
    });

    return res.status(201).json(offer);
  } catch (err) {
    console.error('createOffer error:', err);
    return res.status(500).json({ message: 'Error creando oferta', error: err.message });
  }
}

// Obtener todas las ofertas (para clientes - buscar)
export async function getAllOffers(req, res) {
  try {
    const { category, location, page = 1, limit = 10 } = req.query;
    
    let where = { status: 'active' };
    if (category) where.category = category;
    if (location) where.location = location;

    const offset = (page - 1) * limit;
    
    const offers = await ProfessionalOffer.findAll({
      where,
      include: [
        {
          model: User,
          as: 'professional',
          attributes: ['id', 'name', 'category', 'bio', 'location', 'profilePhotoUrl']
        }
      ],
      limit: parseInt(limit),
      offset: parseInt(offset),
      order: [['createdAt', 'DESC']]
    });

    const total = await ProfessionalOffer.count({ where });

    return res.json({
      offers,
      pagination: {
        total,
        page: parseInt(page),
        pages: Math.ceil(total / limit)
      }
    });
  } catch (err) {
    console.error('getAllOffers error:', err);
    return res.status(500).json({ message: 'Error obteniendo ofertas', error: err.message });
  }
}

// Obtener ofertas del profesional actual
export async function getMyOffers(req, res) {
  try {
    const professionalId = req.user ? req.user.id : null;
    if (!professionalId) {
      return res.status(401).json({ message: 'No autorizado' });
    }

    const offers = await ProfessionalOffer.findAll({
      where: { professionalId },
      order: [['createdAt', 'DESC']]
    });

    return res.json(offers);
  } catch (err) {
    console.error('getMyOffers error:', err);
    return res.status(500).json({ message: 'Error obteniendo ofertas', error: err.message });
  }
}

// Obtener detalle de una oferta
export async function getOfferDetail(req, res) {
  try {
    const { offerId } = req.params;
    
    const offer = await ProfessionalOffer.findByPk(offerId, {
      include: [
        {
          model: User,
          as: 'professional',
          attributes: ['id', 'name', 'category', 'bio', 'location', 'profilePhotoUrl', 'phoneNumber']
        }
      ]
    });

    if (!offer) {
      return res.status(404).json({ message: 'Oferta no encontrada' });
    }

    return res.json(offer);
  } catch (err) {
    console.error('getOfferDetail error:', err);
    return res.status(500).json({ message: 'Error obteniendo detalle', error: err.message });
  }
}

// Actualizar oferta (profesional)
export async function updateOffer(req, res) {
  try {
    const { offerId } = req.params;
    const professionalId = req.user ? req.user.id : null;

    const offer = await ProfessionalOffer.findByPk(offerId);
    if (!offer) {
      return res.status(404).json({ message: 'Oferta no encontrada' });
    }

    if (offer.professionalId !== professionalId) {
      return res.status(403).json({ message: 'No autorizado' });
    }

    const { title, description, category, location, price, currency, availability, image_url, status } = req.body;

    await offer.update({
      title: title || offer.title,
      description: description || offer.description,
      category: category || offer.category,
      location: location || offer.location,
      price: price !== undefined ? price : offer.price,
      currency: currency || offer.currency,
      availability: availability || offer.availability,
      image_url: image_url || offer.image_url,
      status: status || offer.status
    });

    return res.json(offer);
  } catch (err) {
    console.error('updateOffer error:', err);
    return res.status(500).json({ message: 'Error actualizando oferta', error: err.message });
  }
}

// Eliminar oferta (profesional)
export async function deleteOffer(req, res) {
  try {
    const { offerId } = req.params;
    const professionalId = req.user ? req.user.id : null;

    const offer = await ProfessionalOffer.findByPk(offerId);
    if (!offer) {
      return res.status(404).json({ message: 'Oferta no encontrada' });
    }

    if (offer.professionalId !== professionalId) {
      return res.status(403).json({ message: 'No autorizado' });
    }

    await offer.destroy();
    return res.json({ message: 'Oferta eliminada' });
  } catch (err) {
    console.error('deleteOffer error:', err);
    return res.status(500).json({ message: 'Error eliminando oferta', error: err.message });
  }
}

export default {
  createOffer,
  getAllOffers,
  getMyOffers,
  getOfferDetail,
  updateOffer,
  deleteOffer
};
