const fs = require('fs');
const path = require('path');

// Directorio para guardar fotos
const uploadsDir = path.join(__dirname, '../uploads/photos');

// Crear directorio si no existe
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}

/**
 * Convierte una foto en base64 a un archivo y retorna la URL
 * @param {String} base64Data - Datos en base64
 * @param {String} fileName - Nombre del archivo
 * @returns {String} URL relativa del archivo guardado
 */
const savePhotoFromBase64 = (base64Data, fileName) => {
  try {
    // Remover prefijo data:image/...;base64, si existe
    const base64String = base64Data.replace(/^data:image\/\w+;base64,/, '');
    
    // Crear nombre de archivo único
    const timestamp = Date.now();
    const uniqueFileName = `${timestamp}_${fileName}`;
    const filePath = path.join(uploadsDir, uniqueFileName);
    
    // Guardar archivo
    const buffer = Buffer.from(base64String, 'base64');
    fs.writeFileSync(filePath, buffer);
    
    // Retornar URL relativa
    return `/uploads/photos/${uniqueFileName}`;
  } catch (error) {
    console.error('Error al guardar foto:', error);
    throw error;
  }
};

/**
 * Endpoint para convertir y guardar foto en base64
 */
const uploadPhotoBase64 = async (req, res) => {
  try {
    const { base64Data, fileName } = req.body;

    if (!base64Data || !fileName) {
      return res.status(400).json({ error: 'base64Data y fileName son requeridos' });
    }

    const photoUrl = savePhotoFromBase64(base64Data, fileName);

    res.status(200).json({
      success: true,
      photoUrl: photoUrl,
      fullUrl: `${process.env.SERVER_URL || 'http://localhost:3000'}${photoUrl}`
    });
  } catch (error) {
    console.error('Error en uploadPhotoBase64:', error);
    res.status(500).json({ error: 'Error al guardar la foto', message: error.message });
  }
};

/**
 * Endpoint para obtener foto estática
 */
const getPhoto = (req, res) => {
  try {
    const { fileName } = req.params;
    const filePath = path.join(uploadsDir, fileName);

    // Verificar que el archivo existe
    if (!fs.existsSync(filePath)) {
      return res.status(404).json({ error: 'Foto no encontrada' });
    }

    // Enviar archivo
    res.sendFile(filePath);
  } catch (error) {
    console.error('Error al obtener foto:', error);
    res.status(500).json({ error: 'Error al obtener la foto' });
  }
};

module.exports = {
  savePhotoFromBase64,
  uploadPhotoBase64,
  getPhoto
};
