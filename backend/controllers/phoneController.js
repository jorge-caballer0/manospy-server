const crypto = require('crypto');
const nodemailer = require('nodemailer');
const { User, PhoneVerification } = require('../models');

// Helpers
function hashCode(code) {
  return crypto.createHash('sha256').update(code).digest('hex');
}

// Configurar transportador de correo (Gmail u otro SMTP)
const sendEmailWithCode = async (email, code) => {
  try {
    // Usar Gmail con App Password o tu proveedor de correo
    const transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        user: process.env.EMAIL_USER || 'tu_email@gmail.com',
        pass: process.env.EMAIL_PASSWORD || 'tu_app_password'
      }
    });

    const mailOptions = {
      from: process.env.EMAIL_USER || 'tu_email@gmail.com',
      to: email,
      subject: 'Código de Verificación ManosPy',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2 style="color: #137FEC;">Verificación de Teléfono</h2>
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

// POST /auth/phone/verify-request
const requestPhoneVerification = async (req, res) => {
  try {
    const userId = req.user && req.user.id;
    if (!userId) return res.status(401).json({ message: 'No autorizado' });

    const { newPhoneNumber } = req.body;
    if (!newPhoneNumber) return res.status(400).json({ message: 'Falta número' });

    // Obtener email del usuario
    const user = await User.findByPk(userId);
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado' });

    // Generar OTP
    const otp = String(Math.floor(100000 + Math.random() * 900000));
    const codeHash = hashCode(otp);
    const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5 minutos

    const pv = await PhoneVerification.create({
      userId,
      newPhone: newPhoneNumber,
      codeHash,
      method: 'email',
      expiresAt
    });

    // Enviar código por correo
    const sent = await sendEmailWithCode(user.email, otp);

    if (!sent) {
      return res.status(500).json({ message: 'Error al enviar correo' });
    }

    // Devolver solo verificationId (el código se envió por correo, no se devuelve al frontend)
    return res.json({ success: true, verificationId: pv.id, sent: true });
  } catch (error) {
    console.error('Error en requestPhoneVerification', error);
    return res.status(500).json({ message: 'Error interno', error: error.message });
  }
};

// POST /auth/phone/verify
const verifyPhoneWithCode = async (req, res) => {
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

    // Actualizar user phone
    const user = await User.findByPk(pv.userId);
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado' });

    user.phoneNumber = pv.newPhone;
    await user.save();

    pv.used = true;
    await pv.save();

    // Devolver usuario actualizado (omitimos password)
    return res.json({
      id: user.id,
      name: user.name,
      email: user.email,
      phoneNumber: user.phoneNumber,
      role: user.role,
      status: user.status
    });
  } catch (error) {
    console.error('Error en verifyPhoneWithCode', error);
    return res.status(500).json({ message: 'Error interno', error: error.message });
  }
};

module.exports = { requestPhoneVerification, verifyPhoneWithCode };
