const { User } = require('../models'); // importa tu modelo Sequelize
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// Registro de cliente
const registerClient = async (req, res) => {
  try {
    const { name, email, password, phoneNumber } = req.body;

    // Hashear contraseña
    const hashedPassword = await bcrypt.hash(password, 10);

    // Crear cliente en la base de datos
    const user = await User.create({
      name,
      email,
      password: hashedPassword,
      phoneNumber,
      role: 'client',
      status: 'active'
    });

    // Generar token
    const token = jwt.sign(
      { id: user.id, role: user.role },
      process.env.JWT_SECRET,
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
      name,          // nombre completo
      idNumber,      // número de cédula
      email,
      password,
      phoneNumber,
      services,      // servicios que ofrece
      cities,        // ciudades de operación
      documentUrl,   // documentos (PDFs o URLs)
      idFrontUrl,    // foto cédula frente
      idBackUrl,     // foto cédula dorso
      certificates   // fotos o PDFs de certificados
    } = req.body;

    // Hashear contraseña
    const hashedPassword = await bcrypt.hash(password, 10);

    // Crear profesional en la base de datos
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
      status: 'active'
    });

    // Generar token
    const token = jwt.sign(
      { id: user.id, role: user.role },
      process.env.JWT_SECRET,
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
    const { email, password } = req.body;

    // Buscar usuario
    const user = await User.findOne({ where: { email } });
    if (!user) return res.status(404).json({ message: "Usuario no encontrado" });

    // Comparar contraseña
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(401).json({ message: "Contraseña incorrecta" });

    // Generar token
    const token = jwt.sign(
      { id: user.id, role: user.role },
      process.env.JWT_SECRET,
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

module.exports = { registerClient, registerProfessional, login };
