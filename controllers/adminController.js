const { User, Reservation } = require('../models');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { Op } = require('../models');

exports.adminLogin = async (req, res) => {
  const { email, password } = req.body;
  const user = await User.findOne({ where: { email, role: 'admin' } });
  if (user && await bcrypt.compare(password, user.password)) {
    const token = jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET || 'secret_manospy');
    res.json({ token, adminName: user.name });
  } else {
    res.status(401).json({ error: "No autorizado" });
  }
};

exports.getStats = async (req, res) => {
  try {
    const totalClients = await User.count({ where: { role: 'client' } });
    const totalPros = await User.count({ where: { role: 'professional' } });
    const pendingPros = await User.count({ where: { role: 'professional', status: 'pending' } });
    const activeReservations = await Reservation.count({ where: { status: 'active' } });

    res.json({ totalClients, totalPros, pendingPros, activeReservations });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};

exports.getPendingPros = async (req, res) => {
  const profs = await User.findAll({ where: { role: 'professional', status: 'pending' } });
  res.json(profs);
};

exports.approveProfessional = async (req, res) => {
  await User.update({ status: 'active', rejectionReason: null }, { where: { id: req.params.id } });
  res.sendStatus(200);
};

exports.rejectProfessional = async (req, res) => {
  const { reason } = req.body;
  await User.update({ status: 'rejected', rejectionReason: reason }, { where: { id: req.params.id } });
  res.sendStatus(200);
};

exports.getUsers = async (req, res) => {
  const users = await User.findAll({ where: { role: { [Op.ne]: 'admin' } } });
  res.json(users.map(u => ({
    id: u.id,
    name: u.name,
    email: u.email,
    role: u.role,
    status: u.status,
    isBlocked: u.status === 'blocked'
  })));
};

exports.blockUser = async (req, res) => {
  await User.update({ status: 'blocked' }, { where: { id: req.params.id } });
  res.sendStatus(200);
};
