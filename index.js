const express = require('express');
const { Sequelize, DataTypes, Op } = require('sequelize');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

const sequelize = new Sequelize(process.env.DATABASE_URL, {
  dialect: 'postgres',
  dialectOptions: { ssl: { require: true, rejectUnauthorized: false } }
});

// --- MODELOS ---
const User = sequelize.define('User', {
  name: { type: DataTypes.STRING, allowNull: false },
  email: { type: DataTypes.STRING, unique: true, allowNull: false },
  password: { type: DataTypes.STRING, allowNull: false },
  role: { type: DataTypes.STRING, defaultValue: 'client' },
  status: { type: DataTypes.STRING, defaultValue: 'active' }, // active, blocked, pending
  documentUrl: { type: DataTypes.STRING }
});

const ServiceRequest = sequelize.define('ServiceRequest', {
  description: { type: DataTypes.TEXT },
  category: { type: DataTypes.STRING },
  status: { type: DataTypes.STRING, defaultValue: 'pending' }, // pending, accepted, cancelled
  clientId: { type: DataTypes.INTEGER }
});

const Reservation = sequelize.define('Reservation', {
  status: { type: DataTypes.STRING, defaultValue: 'active' }, // active, completed, cancelled
  clientId: { type: DataTypes.INTEGER },
  professionalId: { type: DataTypes.INTEGER },
  serviceRequestId: { type: DataTypes.INTEGER }
});

const Message = sequelize.define('Message', {
  text: { type: DataTypes.TEXT },
  senderId: { type: DataTypes.STRING },
  reservationId: { type: DataTypes.INTEGER }
});

// --- RUTAS DE FUNCIONAMIENTO REAL ---

// Registro con manejo de roles
app.post('/auth/register', async (req, res) => {
  try {
    const { email, password, name, role } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = await User.create({ 
        email, password: hashedPassword, name, 
        role: role || 'client',
        status: role === 'professional' ? 'pending' : 'active'
    });
    const token = jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET);
    res.json({ token, user: { id: user.id, name: user.name, email: user.email, role: user.role } });
  } catch (e) { res.status(400).json({ error: e.message }); }
});

// Profesional ACEPTA una solicitud (Crea Reserva)
app.post('/reservations/accept', async (req, res) => {
    const { serviceRequestId, professionalId } = req.body;
    const sreq = await ServiceRequest.findByPk(serviceRequestId);
    if (sreq && sreq.status === 'pending') {
        sreq.status = 'accepted';
        await sreq.save();
        const reservation = await Reservation.create({
            serviceRequestId,
            professionalId,
            clientId: sreq.clientId,
            status: 'active'
        });
        res.json(reservation);
    } else {
        res.status(400).json({ error: "Solicitud no disponible" });
    }
});

// Admin CANCELA una reserva
app.post('/admin/reservations/:id/cancel', async (req, res) => {
    await Reservation.update({ status: 'cancelled' }, { where: { id: req.params.id } });
    res.sendStatus(200);
});

// --- TODAS LAS DEMÁS RUTAS (Login, Listados, Chat) SE MANTIENEN ---
// [Incluye aquí el resto del código anterior de index.js]

const PORT = process.env.PORT || 10000;
sequelize.sync({ alter: true }).then(async () => {
  app.listen(PORT, () => console.log(`Servidor ManosPy Funcionando en puerto ${PORT}`));
});
