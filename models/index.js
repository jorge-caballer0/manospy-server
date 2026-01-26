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

// Usuario (clientes y profesionales)
const User = sequelize.define('User', {
  name: { type: DataTypes.STRING, allowNull: false },
  email: { type: DataTypes.STRING, unique: true, allowNull: false },
  password: { type: DataTypes.STRING, allowNull: false },
  role: { type: DataTypes.ENUM('client', 'professional', 'admin'), defaultValue: 'client' },
  status: { type: DataTypes.ENUM('active', 'blocked', 'pending', 'rejected'), defaultValue: 'active' },

  // Campos extendidos para profesionales
  phoneNumber: { type: DataTypes.STRING },
  idNumber: { type: DataTypes.STRING }, // Cédula
  category: { type: DataTypes.STRING }, // Oficio principal
  bio: { type: DataTypes.TEXT },
  experience: { type: DataTypes.TEXT },
  location: { type: DataTypes.STRING }, // Ciudad de operación

  services: { type: DataTypes.JSON },       // lista de servicios ofrecidos
  cities: { type: DataTypes.JSON },         // ciudades de operación
  documentUrl: { type: DataTypes.STRING },  // URL a documentos PDF
  idFrontUrl: { type: DataTypes.STRING },   // foto cédula frente
  idBackUrl: { type: DataTypes.STRING },    // foto cédula dorso
  certificates: { type: DataTypes.JSON },   // array de URLs a fotos/PDFs de certificados

  rejectionReason: { type: DataTypes.TEXT } // Motivo si el Admin lo rechaza
});

// Solicitudes de servicioS
const ServiceRequest = sequelize.define('ServiceRequest', {
  description: { type: DataTypes.TEXT },
  category: { type: DataTypes.STRING },
  status: { type: DataTypes.STRING, defaultValue: 'pending' }, // pending, accepted, cancelled
  location: { type: DataTypes.STRING },
  preferredDate: { type: DataTypes.STRING }
});

// Reservas
const Reservation = sequelize.define('Reservation', {
  status: { type: DataTypes.STRING, defaultValue: 'active' } // active, completed, cancelled
});

// Mensajes dentro de una reserva
const Message = sequelize.define('Message', {
  content: { type: DataTypes.TEXT, allowNull: false }, // ⚠️ corregido
  senderId: { type: DataTypes.STRING, allowNull: false },
  timestamp: { type: DataTypes.BIGINT, allowNull: false }
});

// ✅ CORRECCIÓN 6: Modelo de Reviews para calificaciones
const Review = sequelize.define('Review', {
  rating: { type: DataTypes.INTEGER, allowNull: false, min: 1, max: 5 }, // 1-5 estrellas
  comment: { type: DataTypes.TEXT },
  clientId: { type: DataTypes.STRING, allowNull: false },
  professionalId: { type: DataTypes.STRING, allowNull: false },
  reservationId: { type: DataTypes.STRING, allowNull: false }
});

// --- RELACIONES ---
User.hasMany(ServiceRequest, { as: 'requests', foreignKey: 'clientId' });
ServiceRequest.belongsTo(User, { as: 'client', foreignKey: 'clientId' });

ServiceRequest.hasOne(Reservation, { foreignKey: 'serviceRequestId' });
Reservation.belongsTo(ServiceRequest, { foreignKey: 'serviceRequestId' });

User.hasMany(Reservation, { as: 'clientReservations', foreignKey: 'clientId' });
User.hasMany(Reservation, { as: 'profReservations', foreignKey: 'professionalId' });

Reservation.hasMany(Message, { foreignKey: 'reservationId' });
Message.belongsTo(Reservation, { foreignKey: 'reservationId' }); // relación inversa

// ✅ CORRECCIÓN 6: Relaciones para Reviews
User.hasMany(Review, { as: 'reviewsAsClient', foreignKey: 'clientId' });
User.hasMany(Review, { as: 'reviewsAsProf', foreignKey: 'professionalId' });
Review.belongsTo(User, { as: 'client', foreignKey: 'clientId' });
Review.belongsTo(User, { as: 'professional', foreignKey: 'professionalId' });
Reservation.hasMany(Review, { foreignKey: 'reservationId' });
Review.belongsTo(Reservation, { foreignKey: 'reservationId' });

// --- EXPORTAR ---
module.exports = {
  sequelize,
  Sequelize,
  Op,
  User,
  ServiceRequest,
  Reservation,
  Message,
  Review  // ✅ CORRECCIÓN 6: Exportar modelo Review
};

