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
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  name: { type: DataTypes.STRING, allowNull: false },
  email: { type: DataTypes.STRING, unique: true, allowNull: false },
  password: { type: DataTypes.STRING, allowNull: false },
  role: { type: DataTypes.ENUM('client', 'professional', 'admin'), defaultValue: 'client' },
  status: { type: DataTypes.ENUM('active', 'blocked', 'pending', 'rejected'), defaultValue: 'active' },

  // Campos extendidos para profesionales
  phoneNumber: { type: DataTypes.STRING },
  idNumber: { type: DataTypes.STRING }, // Cédula
  profilePhotoUrl: { type: DataTypes.STRING }, // Foto de perfil del profesional
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
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  clientId: { type: DataTypes.INTEGER, allowNull: false }, // INTEGER foreign key
  description: { type: DataTypes.TEXT },
  category: { type: DataTypes.STRING },
  status: { type: DataTypes.STRING, defaultValue: 'pending' }, // pending, accepted, cancelled
  location: { type: DataTypes.STRING },
  preferredDate: { type: DataTypes.STRING }
});

// Reservas
const Reservation = sequelize.define('Reservation', {
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  clientId: { type: DataTypes.INTEGER, allowNull: false }, // INTEGER foreign key
  professionalId: { type: DataTypes.INTEGER, allowNull: false }, // INTEGER foreign key
  serviceRequestId: { type: DataTypes.INTEGER }, // INTEGER foreign key
  status: { type: DataTypes.STRING, defaultValue: 'active' } // active, completed, cancelled
});

// Mensajes dentro de una reserva
const Message = sequelize.define('Message', {
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  reservationId: { type: DataTypes.INTEGER, allowNull: true }, // INTEGER foreign key (nullable for pre-reservation chats)
  chatId: { type: DataTypes.STRING, allowNull: true, field: 'chat_id' }, // chat identifier when message is pre-reservation
  content: { type: DataTypes.TEXT, allowNull: false }, // ⚠️ corregido
  senderId: { type: DataTypes.STRING, allowNull: false },
  timestamp: { type: DataTypes.BIGINT, allowNull: false }
});

// Chats previos a formalizar (conversaciones iniciadas desde una oferta)
const Chat = sequelize.define('Chat', {
  id: {
    type: DataTypes.STRING,
    primaryKey: true
  }
});

// ✅ CORRECCIÓN 6: Modelo de Reviews para calificaciones
const Review = sequelize.define('Review', {
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  reservationId: { type: DataTypes.INTEGER, allowNull: false }, // INTEGER foreign key
  rating: { type: DataTypes.INTEGER, allowNull: false, min: 1, max: 5 }, // 1-5 estrellas
  comment: { type: DataTypes.TEXT },
  clientId: { type: DataTypes.INTEGER, allowNull: false }, // INTEGER foreign key
  professionalId: { type: DataTypes.INTEGER, allowNull: false } // INTEGER foreign key
});

// Modelo para verificación de teléfono (OTPs)
const PhoneVerification = sequelize.define('PhoneVerification', {
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  userId: { type: DataTypes.STRING, allowNull: false },
  newPhone: { type: DataTypes.STRING, allowNull: false },
  codeHash: { type: DataTypes.STRING, allowNull: false },
  method: { type: DataTypes.STRING, allowNull: false, defaultValue: 'sms' },
  expiresAt: { type: DataTypes.DATE, allowNull: false },
  used: { type: DataTypes.BOOLEAN, defaultValue: false },
  attempts: { type: DataTypes.INTEGER, defaultValue: 0 }
});

// ✅ NUEVO: Modelo para direcciones del usuario (mapear a tabla existente 'direcciones')
const Address = sequelize.define('Address', {
  id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
  // La tabla existente 'direcciones' usa INTEGER para user ids
  user_id: { type: DataTypes.INTEGER, allowNull: false },
  direccion: { type: DataTypes.STRING, allowNull: false }, // fullAddress
  ciudad: { type: DataTypes.STRING, allowNull: false },
  estado: { type: DataTypes.STRING },
  codigo_postal: { type: DataTypes.STRING },
  created_at: { type: DataTypes.DATE, allowNull: true } // Timestamp de Supabase
}, { 
  timestamps: false,  // La tabla maneja timestamps manualmente
  tableName: 'direcciones',  // ✅ Mapear a tabla existente
  underscored: true  // Para que use snake_case
});

// ✅ NUEVO: Modelo para ofertas de profesionales
const ProfessionalOffer = sequelize.define('ProfessionalOffer', {
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  professionalId: { type: DataTypes.INTEGER, allowNull: false }, // INTEGER foreign key
  title: { type: DataTypes.STRING, allowNull: false }, // Título de la oferta
  description: { type: DataTypes.TEXT, allowNull: false }, // Descripción detallada
  category: { type: DataTypes.STRING, allowNull: false }, // Categoría/oficio
  location: { type: DataTypes.STRING }, // Ubicación donde ofrece servicio
  price: { type: DataTypes.DECIMAL(10, 2) }, // Precio
  currency: { type: DataTypes.STRING, defaultValue: 'ARS' }, // ARS, USD, etc.
  availability: { type: DataTypes.JSON }, // Disponibilidad {days: [], hours: ""}
  status: { type: DataTypes.STRING, defaultValue: 'active' }, // active, inactive, archived
  image_url: { type: DataTypes.STRING } // URL a imagen de la oferta
});

// --- RELACIONES ---
User.hasMany(Address, { as: 'addresses', foreignKey: 'user_id' });
Address.belongsTo(User, { foreignKey: 'user_id' });

User.hasMany(ServiceRequest, { as: 'requests', foreignKey: 'clientId' });
ServiceRequest.belongsTo(User, { as: 'client', foreignKey: 'clientId' });

ServiceRequest.hasOne(Reservation, { foreignKey: 'serviceRequestId' });
Reservation.belongsTo(ServiceRequest, { foreignKey: 'serviceRequestId' });

User.hasMany(Reservation, { as: 'clientReservations', foreignKey: 'clientId' });
User.hasMany(Reservation, { as: 'profReservations', foreignKey: 'professionalId' });
Reservation.belongsTo(User, { as: 'client', foreignKey: 'clientId' });
Reservation.belongsTo(User, { as: 'professional', foreignKey: 'professionalId' });

Reservation.hasMany(Message, { foreignKey: 'reservationId' });
Message.belongsTo(Reservation, { foreignKey: 'reservationId' }); // relación inversa
// Asociaciones para chat
Chat.hasMany(Message, { foreignKey: 'chatId', sourceKey: 'id' });
Message.belongsTo(Chat, { foreignKey: 'chatId', targetKey: 'id' });

// ✅ Relaciones para ProfessionalOffers
User.hasMany(ProfessionalOffer, { as: 'offers', foreignKey: 'professionalId' });
ProfessionalOffer.belongsTo(User, { as: 'professional', foreignKey: 'professionalId' });

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
  Review,
  Address,
  PhoneVerification,
  Chat,
  ProfessionalOffer
};

