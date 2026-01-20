const { User } = require('../models');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

exports.register = async (req, res) => {
  try {
    const { email, password, name, role, ...extraFields } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);

    const user = await User.create({
      email,
      password: hashedPassword,
      name,
      role: role || 'client',
      status: role === 'professional' ? 'pending' : 'active',
      ...extraFields
    });

    const token = jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET || 'secret_manospy');
    res.json({ token, user: { id: user.id, name: user.name, email: user.email, role: user.role, status: user.status } });
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};

exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;
    const user = await User.findOne({ where: { email } });

    if (user && await bcrypt.compare(password, user.password)) {
      if (user.status === 'blocked') return res.status(403).json({ error: "Cuenta bloqueada por administración" });

      const token = jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET || 'secret_manospy');
      res.json({ token, user });
    } else {
      res.status(401).json({ error: "Credenciales inválidas" });
    }
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};
