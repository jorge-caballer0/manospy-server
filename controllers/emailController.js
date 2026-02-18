const crypto = require('crypto');
const nodemailer = require('nodemailer');
const { User, PhoneVerification } = require('../models');

// Helpers
function hashCode(code) {
  return crypto.createHash('sha256').update(code).digest('hex');
}

// Configurar transportador de correo
const sendEmailWithCode = async (email, code, type = 'verify') => {
  try {
    const transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        user: process.env.EMAIL_USER || 'tu_email@gmail.com',
        pass: process.env.EMAIL_PASSWORD || 'tu_app_password'
      }
    });

    const subject = type === 'verify' ? 'Código de Verificación de Correo' : 'Código para Cambiar Correo';
    const title = type === 'verify' ? 'Verificación de Correo' : 'Cambio de Correo';

    const mailOptions = {
      from: process.env.EMAIL_USER || 'tu_email@gmail.com',
      to: email,
      subject: `${subject} - ManosPy`,
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2 style="color: #137FEC;">${title}</h2>
          <p>Hola,</p>
          <p>Tu código de verificación es:</p>
          <h1 style="color: #137FEC; letter-spacing: 5px; text-align: center;">${code}</h1>
          <p style="color: #666;">Este código expira en 5 minutos.</p>
          <p style="color: #999; font-size: 12px;">Si no solicitaste este código, ignora este mensaje.</p>
        </div>
      `
    };

    await transporter.sendMail(mailOptions);
    return true;
  } catch (error) {
    console.error('Error al enviar correo:', error);
    return false;
  }
};

// POST /auth/email/verify-request (Verificar correo actual)
const requestEmailVerification = async (req, res) => {
  try {
    const userId = req.user && req.user.id;
    if (!userId) return res.status(401).json({ message: 'No autorizado' });

    const user = await User.findByPk(userId);
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado' });

    // Generar OTP
    const otp = String(Math.floor(100000 + Math.random() * 900000));
    const codeHash = hashCode(otp);
    const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5 minutos

    const pv = await PhoneVerification.create({
      userId,
      newPhone: user.email, // Reutilizar campo para guardar el email
      codeHash,
      method: 'email_verify',
      expiresAt
    });

    // Enviar código por correo
    const sent = await sendEmailWithCode(user.email, otp, 'verify');

    if (!sent) {
      return res.status(500).json({ message: 'Error al enviar correo' });
    }

    return res.json({ success: true, verificationId: pv.id, sent: true });
  } catch (error) {
    console.error('Error en requestEmailVerification', error);
    return res.status(500).json({ message: 'Error interno', error: error.message });
  }
};

// POST /auth/email/verify (Verificar código de correo actual)
const verifyEmailCode = async (req, res) => {
  try {
    const { verificationId, code } = req.body;
    if (!verificationId || !code) return res.status(400).json({ message: 'Faltan parámetros' });

    const pv = await PhoneVerification.findByPk(verificationId);
    if (!pv) return res.status(404).json({ message: 'Verificación no encontrada' });
    if (pv.used) return res.status(400).json({ message: 'Verificación ya usada' });
    if (new Date(pv.expiresAt) < new Date()) return res.status(400).json({ message: 'Código expirado' });
    if (pv.attempts >= 5) return res.status(400).json({ message: 'Demasiados intentos' });

    const matches = hashCode(code) === pv.codeHash;
    if (!matches) {
      pv.attempts = pv.attempts + 1;
      await pv.save();
      return res.status(400).json({ message: 'Código incorrecto' });
    }

    pv.used = true;
    await pv.save();

    const user = await User.findByPk(pv.userId);
    return res.json({
      id: user.id,
      name: user.name,
      email: user.email,
      emailVerified: true,
      phoneNumber: user.phoneNumber,
      role: user.role
    });
  } catch (error) {
    console.error('Error en verifyEmailCode', error);
    return res.status(500).json({ message: 'Error interno', error: error.message });
  }
};

// POST /auth/email/change-request (Cambiar correo)
const requestEmailChange = async (req, res) => {
  try {
    const userId = req.user && req.user.id;
    if (!userId) return res.status(401).json({ message: 'No autorizado' });

    const { newEmail } = req.body;
    if (!newEmail) return res.status(400).json({ message: 'Falta nuevo correo' });

    const user = await User.findByPk(userId);
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado' });

    // Verificar que el nuevo correo no esté en uso
    const existingUser = await User.findOne({ where: { email: newEmail } });
    if (existingUser) return res.status(400).json({ message: 'El correo ya está en uso' });

    // Generar OTP
    const otp = String(Math.floor(100000 + Math.random() * 900000));
    const codeHash = hashCode(otp);
    const expiresAt = new Date(Date.now() + 5 * 60 * 1000);

    const pv = await PhoneVerification.create({
      userId,
      newPhone: newEmail, // Guardar el nuevo email en este campo
      codeHash,
      method: 'email_change',
      expiresAt
    });

    // Enviar código al nuevo correo
    const sent = await sendEmailWithCode(newEmail, otp, 'change');

    if (!sent) {
      return res.status(500).json({ message: 'Error al enviar correo' });
    }

    return res.json({ success: true, verificationId: pv.id, sent: true });
  } catch (error) {
    console.error('Error en requestEmailChange', error);
    return res.status(500).json({ message: 'Error interno', error: error.message });
  }
};

// POST /auth/email/change-verify (Verificar cambio de correo)
const verifyEmailChange = async (req, res) => {
  try {
    const { verificationId, code } = req.body;
    if (!verificationId || !code) return res.status(400).json({ message: 'Faltan parámetros' });

    const pv = await PhoneVerification.findByPk(verificationId);
    if (!pv) return res.status(404).json({ message: 'Verificación no encontrada' });
    if (pv.used) return res.status(400).json({ message: 'Verificación ya usada' });
    if (new Date(pv.expiresAt) < new Date()) return res.status(400).json({ message: 'Código expirado' });
    if (pv.attempts >= 5) return res.status(400).json({ message: 'Demasiados intentos' });

    const matches = hashCode(code) === pv.codeHash;
    if (!matches) {
      pv.attempts = pv.attempts + 1;
      await pv.save();
      return res.status(400).json({ message: 'Código incorrecto' });
    }

    // Actualizar email del usuario
    const user = await User.findByPk(pv.userId);
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado' });

    user.email = pv.newPhone; // newPhone guarda el nuevo email
    await user.save();

    pv.used = true;
    await pv.save();

    return res.json({
      id: user.id,
      name: user.name,
      email: user.email,
      phoneNumber: user.phoneNumber,
      role: user.role
    });
  } catch (error) {
    console.error('Error en verifyEmailChange', error);
    return res.status(500).json({ message: 'Error interno', error: error.message });
  }
};

module.exports = {
  requestEmailVerification,
  verifyEmailCode,
  requestEmailChange,
  verifyEmailChange
};
