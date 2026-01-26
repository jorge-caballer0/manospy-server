const { ServiceRequest, User, Reservation } = require('../models');

exports.getPendingRequests = async (req, res) => {
  try {
    const requests = await ServiceRequest.findAll({
      where: { status: 'pending' },
      include: [{ model: User, as: 'client', attributes: ['name'] }]
    });
    res.json(requests);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};

exports.createRequest = async (req, res) => {
  try {
    const reqData = await ServiceRequest.create(req.body);
    res.json(reqData);
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};

// ✅ CORRECCIÓN 8: Obtener detalle de solicitud de servicio
exports.getServiceById = async (req, res) => {
  try {
    const { id } = req.params;
    const service = await ServiceRequest.findByPk(id, {
      include: [{ model: User, as: 'client', attributes: ['id', 'name', 'email', 'phoneNumber'] }]
    });
    if (!service) {
      return res.status(404).json({ error: "Solicitud no encontrada" });
    }
    res.json(service);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};

// ✅ CORRECCIÓN 8: Aceptar solicitud de servicio (crear reserva)
exports.acceptService = async (req, res) => {
  try {
    const { id } = req.params;
    const professionalId = req.user.id; // Del JWT decoded

    const serviceRequest = await ServiceRequest.findByPk(id);
    if (!serviceRequest) {
      return res.status(404).json({ error: "Solicitud no encontrada" });
    }

    if (serviceRequest.status !== 'pending') {
      return res.status(400).json({ error: "Solicitud no está pendiente" });
    }

    // Crear reserva
    const reservation = await Reservation.create({
      serviceRequestId: id,
      clientId: serviceRequest.clientId,
      professionalId: professionalId,
      status: 'active'
    });

    // Actualizar estado de solicitud
    await serviceRequest.update({ status: 'accepted' });

    res.json(reservation);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};

