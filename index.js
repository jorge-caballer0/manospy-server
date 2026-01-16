const express = require('express');
const { Sequelize, DataTypes, Op } = require('sequelize');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

// Configuración de la Base de Datos (Render usará DATABASE_URL)
const sequelize = new Sequelize(process.env.DATABASE_URL, {
  dialect: 'postgres',
  dialectOptions: { ssl: { require: true, rejectUnauthorized: false } }
});

// --- MODELOS ---
const User = sequelize.define('User', {
  name: { type: DataTypes.STRING, allowNull: false },
  email: { type: DataTypes.STRING, unique: true, allowNull: false },
  password: { type: DataTypes.STRING, allowNull: false },
  role: { type: DataTypes.STRING, defaultValue: 'client' }, // client, professional, admin
  status: { type: DataTypes.STRING, defaultValue: 'active' }, // active, blocked, pending
  documentUrl: { type: DataTypes.STRING }
});

const ServiceRequest = sequelize.define('ServiceRequest', {
  description: { type: DataTypes.TEXT },
  category: { type: DataTypes.STRING },
  status: { type: DataTypes.STRING, defaultValue: 'pending' }
});

const Reservation = sequelize.define('Reservation', {
  status: { type: DataTypes.STRING, defaultValue: 'active' }
});

const Message = sequelize.define('Message', {
  text: { type: DataTypes.TEXT },
  senderId: { type: DataTypes.STRING }
});

// Relaciones
User.hasMany(ServiceRequest, { as: 'requests', foreignKey: 'clientId' });
ServiceRequest.belongsTo(User, { as: 'client', foreignKey: 'clientId' });

ServiceRequest.hasOne(Reservation, { foreignKey: 'serviceRequestId' });
Reservation.belongsTo(ServiceRequest, { foreignKey: 'serviceRequestId' });

User.hasMany(Reservation, { as: 'clientReservations', foreignKey: 'clientId' });
User.hasMany(Reservation, { as: 'profReservations', foreignKey: 'professionalId' });

Reservation.hasMany(Message, { foreignKey: 'reservationId' });

// --- RUTAS ---

// Registro y Login
app.post('/auth/register', async (req, res) => {
  try {
    const { email, password, name, role } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = await User.create({ 
        email, 
        password: hashedPassword, 
        name, 
        role: role || 'client',
        status: role === 'professional' ? 'pending' : 'active'
    });
    const token = jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET);
    res.json({ token, user: { id: user.id, name: user.name, email: user.email, role: user.role } });
  } catch (e) { res.status(400).json({ error: e.message }); }
});

app.post('/auth/login', async (req, res) => {
  const { email, password } = req.body;
  const user = await User.findOne({ where: { email, status: 'active' } });
  if (user && await bcrypt.compare(password, user.password)) {
    const token = jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET);
    res.json({ token, user: { id: user.id, name: user.name, email: user.email, role: user.role } });
  } else { res.status(401).json({ error: "Credenciales inválidas o cuenta bloqueada" }); }
});

// --- RUTAS ADMIN ---
app.post('/admin/login', async (req, res) => {
    const { email, password } = req.body;
    const user = await User.findOne({ where: { email, role: 'admin' } });
    if (user && await bcrypt.compare(password, user.password)) {
      const token = jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET);
      res.json({ token, adminName: user.name });
    } else { res.status(401).json({ error: "No autorizado" }); }
});

app.get('/admin/professionals/pending', async (req, res) => {
    const profs = await User.findAll({ where: { role: 'professional', status: 'pending' } });
    res.json(profs);
});

app.post('/admin/professionals/:id/approve', async (req, res) => {
    await User.update({ status: 'active' }, { where: { id: req.params.id } });
    res.sendStatus(200);
});

app.get('/admin/users', async (req, res) => {
    const users = await User.findAll({ where: { role: { [Op.ne]: 'admin' } } });
    res.json(users.map(u => ({ id: u.id, name: u.name, role: u.role, isBlocked: u.status === 'blocked' })));
});

app.post('/admin/users/:id/block', async (req, res) => {
    await User.update({ status: 'blocked' }, { where: { id: req.params.id } });
    res.sendStatus(200);
});

// --- RUTAS DE SERVICIOS ---
app.get('/service-requests', async (req, res) => {
    const requests = await ServiceRequest.findAll({ where: { status: 'pending' } });
    res.json(requests);
});

app.post('/service-requests', async (req, res) => {
    const reqData = await ServiceRequest.create(req.body);
    res.json(reqData);
});

app.get('/reservations', async (req, res) => {
    const reservations = await Reservation.findAll();
    res.json(reservations);
});

app.get('/reservations/:id/messages', async (req, res) => {
    const messages = await Message.findAll({ where: { reservationId: req.params.id } });
    res.json(messages);
});

app.post('/reservations/:id/messages', async (req, res) => {
    const msg = await Message.create({ ...req.body, reservationId: req.params.id });
    res.json(msg);
});

// --- INICIO ---
const PORT = process.env.PORT || 10000;
sequelize.sync({ alter: true }).then(async () => {
  // Crear Admin por defecto si no existe
  const adminExists = await User.findOne({ where: { role: 'admin' } });
  if (!adminExists) {
    const hashedPassword = await bcrypt.hash('admin123', 10);
    await User.create({ 
        name: 'Super Admin', 
        email: 'admin@manospy.com', 
        password: hashedPassword, 
        role: 'admin', 
        status: 'active' 
    });
    console.log("Cuenta Admin creada: admin@manospy.com / admin123");
  }
  
  app.listen(PORT, () => console.log(`Servidor ManosPy en puerto ${PORT}`));
});