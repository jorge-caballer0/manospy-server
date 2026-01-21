const registerClient = async (req, res) => {
  try {
    const { name, email, password, phoneNumber } = req.body;
    // lógica para crear cliente
    res.status(200).json({ user, token });
  } catch (error) {
    res.status(500).json({ message: "Error en registro de cliente", error });
  }
};

const registerProfessional = async (req, res) => {
  try {
    const { name, email, password, oficio } = req.body;
    // lógica para crear profesional
    res.status(200).json({ user, token });
  } catch (error) {
    res.status(500).json({ message: "Error en registro de profesional", error });
  }
};

const login = async (req, res) => {
  // tu lógica de login
};

module.exports = { registerClient, registerProfessional, login };
