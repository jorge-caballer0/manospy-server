const User = sequelize.define('User', {
  name: { type: DataTypes.STRING, allowNull: false },
  email: { type: DataTypes.STRING, unique: true, allowNull: false },
  password: { type: DataTypes.STRING, allowNull: false },
  role: { type: DataTypes.ENUM('client', 'professional', 'admin'), defaultValue: 'client' },
  status: { type: DataTypes.ENUM('active', 'blocked', 'pending', 'rejected'), defaultValue: 'active' },

  // Campos extendidos para clientes y profesionales
  phoneNumber: { type: DataTypes.STRING },
  idNumber: { type: DataTypes.STRING }, // Cédula
  category: { type: DataTypes.STRING }, // Oficio principal
  bio: { type: DataTypes.TEXT },
  experience: { type: DataTypes.TEXT },
  location: { type: DataTypes.STRING }, // Ciudad de operación

  // Documentos y archivos
  services: { type: DataTypes.JSON },       // lista de servicios ofrecidos
  cities: { type: DataTypes.JSON },         // ciudades de operación
  documentUrl: { type: DataTypes.STRING },  // URL a documentos PDF
  idFrontUrl: { type: DataTypes.STRING },   // foto cédula frente
  idBackUrl: { type: DataTypes.STRING },    // foto cédula dorso
  certificates: { type: DataTypes.JSON },   // array de URLs a fotos/PDFs de certificados

  rejectionReason: { type: DataTypes.TEXT } // Motivo si el Admin lo rechaza
});
