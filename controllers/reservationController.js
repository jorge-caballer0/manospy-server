const { Reservation, ServiceRequest, User, Message } = require('../models');

// Obtener todas las reservas con info del cliente
exports.getReservations = async (req, res) => {
  try {
    const reservations = await Reservation.findAll({
      include: [
        {
          model: ServiceRequest,
          include: [{ model: User, as: 'client', attributes: ['id', 'name', 'email'] }]
        }
      ]
    });
    res.json(reservations);
  } catch (e) {
    console.error("Error en getReservations:", e);
    res.status(500).json({ error: e.message });
  }
};

// Crear nueva reserva (cuando un cliente solicita un servicio)
exports.createReservation = async (req, res) => {
  try {
    const { clientId, professionalId, serviceId, scheduledDate } = req.body;

    const reservation = await Reservation.create({
      clientId,
      professionalId,
      serviceId,
      scheduledDate,
      status: 'pending'
    });

    res.status(201).json(reservation);
  } catch (e) {
    console.error("Error en createReservation:", e);
    res.status(400).json({ error: e.message });
  }
};

// Profesional acepta la reserva
exports.acceptReservation = async (req, res) => {
  try {
    await Reservation.update(
      { status: 'active' },
      { where: { id: req.params.id } }
    );
    res.json({ message: "Reserva aceptada" });
  } catch (e) {
    console.error("Error en acceptReservation:", e);
    res.status(400).json({ error: e.message });
  }
};

// Profesional rechaza la reserva
exports.rejectReservation = async (req, res) => {
  try {
    await Reservation.update(
      { status: 'rejected' },
      { where: { id: req.params.id } }
    );
    res.json({ message: "Reserva rechazada" });
  } catch (e) {
    console.error("Error en rejectReservation:", e);
    res.status(400).json({ error: e.message });
  }
};

// Obtener mensajes de una reserva
exports.getMessages = async (req, res) => {
  try {
    const messages = await Message.findAll({
      where: { reservationId: req.params.id },
      order: [['timestamp', 'ASC']]
    });

    // Convertir timestamp a hora local Asunción al devolver
    const formatted = messages.map(m => ({
      ...m.toJSON(),
      formattedTime: new Date(Number(m.timestamp)).toLocaleString("es-PY", { timeZone: "America/Asuncion" })
    }));

    res.json(formatted);
  } catch (e) {
    console.error("Error en getMessages:", e);
    res.status(500).json({ error: e.message });
  }
};

// Agregar mensaje al chat de una reserva
exports.addMessage = async (req, res) => {
  try {
    const { senderId, content } = req.body;

    const msg = await Message.create({
      senderId,
      reservationId: req.params.id,
      content,
      timestamp: Date.now() // BIGINT válido en UTC
    });

    res.json(msg);
  } catch (e) {
    console.error("Error en addMessage:", e);
    res.status(400).json({ error: e.message });
  }
};

