// models/index.js
const { Sequelize, DataTypes, Op } = require('sequelize');
const bcrypt = require('bcryptjs');

// Conexión a la base de datos (Render usará DATABASE_URL)
const sequelize = new Sequelize(process.env.DATABASE_URL, {
  dialect: 'postgres',
  dialectOptions: {
    ssl: {
      require: true,
      rejectUnauthorized: false
    }
  },
  logging: false
});

// --- MODELOS ---

const User = sequelize.define('User', {
  name: { type: DataTypes.STRING, allowNull: false },
  email: { type: DataTypes.STRING, unique: true, allowNull: false },
  password: { type: DataTypes.STRING, allowNull: false },
  role: { type: DataTypes.STRING, defaultValue: 'client' }, // client, professional, admin
  status: { type: DataTypes.STRING, defaultValue: 'active' }, // active, blocked, pending, rejected

  // Campos extendidos para el Registro Profesional Premium
  phoneNumber: { type: DataTypes.STRING },
  idNumber: { type: DataTypes.STRING }, // Cédula
  category: { type: DataTypes.STRING }, // Oficio principal
  bio: { type: DataTypes.TEXT },
  experience: { type: DataTypes.STRING },
  location: { type: DataTypes.STRING }, // Ciudad de operación
  documentUrl: { type: DataTypes.STRING }, // Foto de Cédula/ID
  rejectionReason: { type: DataTypes.TEXT } // Motivo si el Admin lo rechaza
});

const ServiceRequest = sequelize.define('ServiceRequest', {
  description: { type: DataTypes.TEXT },
  category: { type: DataTypes.STRING },
  status: { type: DataTypes.STRING, defaultValue: 'pending' }, // pending, accepted, cancelled
  location: { type: DataTypes.STRING },
  preferredDate: { type: DataTypes.STRING }
});

const Reservation = sequelize.define('Reservation', {
  status: { type: DataTypes.STRING, defaultValue: 'active' } // active, completed, cancelled
});

const Message = sequelize.define('Message', {
  text: { type: DataTypes.TEXT },
  senderId: { type: DataTypes.STRING },
  timestamp: { type: DataTypes.BIGINT }
});

// --- RELACIONES ---
User.hasMany(ServiceRequest, { as: 'requests', foreignKey: 'clientId' });
ServiceRequest.belongsTo(User, { as: 'client', foreignKey: 'clientId' });

ServiceRequest.hasOne(Reservation, { foreignKey: 'serviceRequestId' });
Reservation.belongsTo(ServiceRequest, { foreignKey: 'serviceRequestId' });

User.hasMany(Reservation, { as: 'clientReservations', foreignKey: 'clientId' });
User.hasMany(Reservation, { as: 'profReservations', foreignKey: 'professionalId' });

Reservation.hasMany(Message, { foreignKey: 'reservationId' });

// Exportar todo
module.exports = {
  sequelize,
  Sequelize,
  Op,
  User,
  ServiceRequest,
  Reservation,
  Message
};
