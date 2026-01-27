const { User } = require('../models'); // importa tu modelo Sequelize
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// Registro de cliente
const registerClient = async (req, res) => {
  try {
    const { name, email, password, phoneNumber } = req.body;

    const hashedPassword = await bcrypt.hash(password, 10);

    const user = await User.create({
      name,
      email,
      password: hashedPassword,
      phoneNumber,
      role: 'client',
      status: 'active'
    });

    const token = jwt.sign(
      { id: user.id, role: user.role },
      process.env.JWT_SECRET || 'secret_manospy',
      { expiresIn: '1d' }
    );

    res.status(200).json({
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        phoneNumber: user.phoneNumber,
        role: user.role,
        status: user.status
      },
      token
    });
  } catch (error) {
    console.error("Error en registro de cliente:", error);
    res.status(500).json({ message: "Error en registro de cliente", error: error.message });
  }
};

// Registro de profesional
const registerProfessional = async (req, res) => {
  try {
    const {
      name,
      idNumber,
      email,
      password,
      phoneNumber,
      services,
      cities,
      documentUrl,
      idFrontUrl,
      idBackUrl,
      certificates
    } = req.body;

    const hashedPassword = await bcrypt.hash(password, 10);

    const user = await User.create({
      name,
      idNumber,
      email,
      password: hashedPassword,
      phoneNumber,
      services,
      cities,
      documentUrl,
      idFrontUrl,
      idBackUrl,
      certificates,
      role: 'professional',
      status: 'pending'
    });

    const token = jwt.sign(
      { id: user.id, role: user.role },
      process.env.JWT_SECRET || 'secret_manospy',
      { expiresIn: '1d' }
    );

    res.status(200).json({
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        phoneNumber: user.phoneNumber,
        idNumber: user.idNumber,
        services: user.services,
        cities: user.cities,
        documentUrl: user.documentUrl,
        idFrontUrl: user.idFrontUrl,
        idBackUrl: user.idBackUrl,
        certificates: user.certificates,
        role: user.role,
        status: user.status
      },
      token
    });
  } catch (error) {
    console.error("Error en registro de profesional:", error);
    res.status(500).json({ message: "Error en registro de profesional", error: error.message });
  }
};

// Login
const login = async (req, res) => {
  try {
    const { email, password, role } = req.body;

    const user = await User.findOne({ where: { email } });
    if (!user) return res.status(404).json({ message: "Usuario no encontrado" });

    // Validar que el role coincida (si se envía)
    if (role && user.role !== role) {
      return res.status(403).json({ message: `Usuario no es ${role}` });
    }

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(401).json({ message: "Contraseña incorrecta" });

    if (user.role === 'professional' && user.status !== 'active') {
      return res.status(403).json({ message: "Cuenta pendiente de validación por admin" });
    }

    const token = jwt.sign(
      { id: user.id, role: user.role },
      process.env.JWT_SECRET || 'secret_manospy',
      { expiresIn: '1d' }
    );

    res.status(200).json({
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        phoneNumber: user.phoneNumber,
        role: user.role,
        status: user.status
      },
      token
    });
  } catch (error) {
    console.error("Error en login:", error);
    res.status(500).json({ message: "Error en login", error: error.message });
  }
};

// Obtener usuario actual (/auth/me)
const getCurrentUser = async (req, res) => {
  try {
    const userId = req.user.id;
    const user = await User.findByPk(userId);

    if (!user) return res.status(404).json({ message: "Usuario no encontrado" });

    res.status(200).json({
      id: user.id,
      name: user.name,
      email: user.email,
      phoneNumber: user.phoneNumber,
      role: user.role,
      status: user.status
    });
  } catch (error) {
    console.error("Error en /auth/me:", error);
    res.status(500).json({ message: "Error al obtener usuario", error: error.message });
  }
};

module.exports = { registerClient, registerProfessional, login, getCurrentUser };
