// middleware/adminMiddleware.js
// ✅ CORRECCIÓN 5: Crear middleware de autorización para admin

const adminMiddleware = (req, res, next) => {
  // Primero verificamos que sea un usuario autenticado (generalmente después de authMiddleware)
  if (!req.user) {
    return res.status(401).json({ message: "No autenticado" });
  }

  // Verificamos que el rol sea 'admin'
  if (req.user.role !== 'admin') {
    return res.status(403).json({ message: "Acceso denegado: se requiere rol admin" });
  }

  // Si pasó la validación, continuar al siguiente middleware
  next();
};

export default adminMiddleware;
