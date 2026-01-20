const { Reservation, ServiceRequest, User, Message } = require('../models');

exports.getReservations = async (req, res) => {
  try {
    const reservations = await Reservation.findAll({
      include: [
        { model: ServiceRequest, include: [{ model: User, as: 'client', attributes: ['name'] }] }
      ]
    });
    res.json(reservations);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};

exports.getMessages = async (req, res) => {
  try {
    const messages = await Message.findAll({
      where: { reservationId: req.params.id },
      order: [['timestamp', 'ASC']]
    });
    res.json(messages);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};

exports.addMessage = async (req, res) => {
  try {
    const msg = await Message.create({ ...req.body, reservationId: req.params.id });
    res.json(msg);
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};
