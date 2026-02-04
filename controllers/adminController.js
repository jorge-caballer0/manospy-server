const { User, Reservation, ServiceCategory } = require('../models');
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

// ✅ CATEGORÍAS - OBTENER TODAS
exports.getCategories = async (req, res) => {
  try {
    const categories = await ServiceCategory.findAll({
      order: [['name', 'ASC']]
    });
    res.json(categories);
  } catch (error) {
    console.error("Error en getCategories:", error);
    res.status(500).json({ message: "Error al obtener categorías", error: error.message });
  }
};

// ✅ CATEGORÍAS - CREAR
exports.createCategory = async (req, res) => {
  try {
    const { name, description, icon } = req.body;

    if (!name || name.trim() === '') {
      return res.status(400).json({ error: "El nombre de la categoría es requerido" });
    }

    const existingCategory = await ServiceCategory.findOne({ where: { name } });
    if (existingCategory) {
      return res.status(409).json({ error: "La categoría ya existe" });
    }

    const category = await ServiceCategory.create({
      name: name.trim(),
      description: description || '',
      icon: icon || 'default'
    });

    res.status(201).json(category);
  } catch (error) {
    console.error("Error en createCategory:", error);
    res.status(500).json({ message: "Error al crear categoría", error: error.message });
  }
};

// ✅ CATEGORÍAS - ACTUALIZAR
exports.updateCategory = async (req, res) => {
  try {
    const { id } = req.params;
    const { name, description, icon } = req.body;

    const category = await ServiceCategory.findByPk(id);
    if (!category) {
      return res.status(404).json({ error: "Categoría no encontrada" });
    }

    await category.update({
      name: name || category.name,
      description: description !== undefined ? description : category.description,
      icon: icon || category.icon
    });

    res.json(category);
  } catch (error) {
    console.error("Error en updateCategory:", error);
    res.status(500).json({ message: "Error al actualizar categoría", error: error.message });
  }
};

// ✅ CATEGORÍAS - ELIMINAR
exports.deleteCategory = async (req, res) => {
  try {
    const { id } = req.params;

    const category = await ServiceCategory.findByPk(id);
    if (!category) {
      return res.status(404).json({ error: "Categoría no encontrada" });
    }

    await category.destroy();
    res.json({ message: "Categoría eliminada exitosamente" });
  } catch (error) {
    console.error("Error en deleteCategory:", error);
    res.status(500).json({ message: "Error al eliminar categoría", error: error.message });
  }
};

// ✅ OBTENER DASHBOARD - ESTADÍSTICAS
exports.getDashboardStats = async (req, res) => {
  try {
    const totalClients = await User.count({ where: { role: 'client' } });
    const totalProfessionals = await User.count({ where: { role: 'professional' } });
    const pendingProfessionals = await User.count({ where: { role: 'professional', status: 'pending' } });
    const approvedProfessionals = await User.count({ where: { role: 'professional', status: 'active' } });
    const totalReservations = await Reservation.count();
    const activeReservations = await Reservation.count({ where: { status: 'active' } });
    const totalCategories = await ServiceCategory.count();

    res.json({
      totalClients,
      totalProfessionals,
      pendingProfessionals,
      approvedProfessionals,
      totalReservations,
      activeReservations,
      totalCategories
    });
  } catch (error) {
    console.error("Error en getDashboardStats:", error);
    res.status(500).json({ message: "Error al obtener estadísticas", error: error.message });
  }
};

