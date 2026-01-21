const { User, Reservation } = require('../models');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { Op } = require('sequelize'); // ⚠️ corregido

// Login de administrador
exports.adminLogin = async (req, res) => {
  try {
    const { email, password } = req.body;
    const user = await User.findOne({ where: { email, role: 'admin' } });

    if (user && await bcrypt.compare(password, user.password)) {
      const token = jwt.sign(
        { id: user.id, role: user.role },
        process.env.JWT_SECRET || 'secret_manospy',
        { expiresIn: '1d' }
      );
      res.json({ token, adminName: user.name });
    } else {
      res.status(401).json({ error: "No autorizado" });
    }
  } catch (error) {
    console.error("Error en adminLogin:", error);
    res.status(500).json({ message: "Error en login admin", error: error.message });
  }
};

// Estadísticas generales
exports.getStats = async (req, res) => {
  try {
    const totalClients = await User.count({ where: { role: 'client' } });
    const totalPros = await User.count({ where: { role: 'professional' } });
    const pendingPros = await User.count({ where: { role: 'professional', status: 'pending' } });
    const activeReservations = await Reservation.count({ where: { status: 'active' } });

    res.json({ totalClients, totalPros, pendingPros, activeReservations });
  } catch (error) {
    console.error("Error en getStats:", error);
    res.status(500).json({ message: "Error al obtener estadísticas", error: error.message });
  }
};

// Profesionales pendientes
exports.getPendingPros = async (req, res) => {
  try {
    const profs = await User.findAll({ where: { role: 'professional', status: 'pending' } });
    res.json(profs);
  } catch (error) {
    console.error("Error en getPendingPros:", error);
    res.status(500).json({ message: "Error al obtener profesionales pendientes", error: error.message });
  }
};

// Aprobar profesional
exports.approveProfessional = async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (Number.isNaN(id)) {
      return res.status(400).json({ message: "ID inválido. Debe ser un número entero." });
    }

    const [rows] = await User.update(
      { status: 'active', rejectionReason: null },
      { where: { id } }
    );

    if (rows === 0) {
      return res.status(404).json({ message: "Profesional no encontrado" });
    }

    const user = await User.findByPk(id);
    res.json({ message: "Profesional aprobado", user });
  } catch (error) {
    console.error("Error en approveProfessional:", error);
    res.status(500).json({ message: "Error al aprobar profesional", error: error.message });
  }
};

// Rechazar profesional
exports.rejectProfessional = async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (Number.isNaN(id)) {
      return res.status(400).json({ message: "ID inválido. Debe ser un número entero." });
    }

    const { reason } = req.body;
    const [rows] = await User.update(
      { status: 'rejected', rejectionReason: reason },
      { where: { id } }
    );

    if (rows === 0) {
      return res.status(404).json({ message: "Profesional no encontrado" });
    }

    const user = await User.findByPk(id);
    res.json({ message: "Profesional rechazado", user });
  } catch (error) {
    console.error("Error en rejectProfessional:", error);
    res.status(500).json({ message: "Error al rechazar profesional", error: error.message });
  }
};

// Obtener todos los usuarios (excepto admin)
exports.getUsers = async (req, res) => {
  try {
    const users = await User.findAll({ where: { role: { [Op.ne]: 'admin' } } });
    res.json(users.map(u => ({
      id: u.id,
      name: u.name,
      email: u.email,
      role: u.role,
      status: u.status,
      isBlocked: u.status === 'blocked'
    })));
  } catch (error) {
    console.error("Error en getUsers:", error);
    res.status(500).json({ message: "Error al obtener usuarios", error: error.message });
  }
};

// Bloquear usuario
exports.blockUser = async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (Number.isNaN(id)) {
      return res.status(400).json({ message: "ID inválido. Debe ser un número entero." });
    }

    const [rows] = await User.update(
      { status: 'blocked' },
      { where: { id } }
    );

    if (rows === 0) {
      return res.status(404).json({ message: "Usuario no encontrado" });
    }

    const user = await User.findByPk(id);
    res.json({ message: "Usuario bloqueado", user });
  } catch (error) {
    console.error("Error en blockUser:", error);
    res.status(500).json({ message: "Error al bloquear usuario", error: error.message });
  }
};
