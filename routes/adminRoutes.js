import express from 'express';
import authMiddleware from '../middleware/authMiddleware.js';
import adminMiddleware from '../middleware/adminMiddleware.js';
import {
  adminLogin,
  getStats,
  getPendingPros,
  approveProfessional,
  rejectProfessional,
  getUsers,
  blockUser,
  unblockUser,
  getAllReservations,
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  getDashboardStats
} from '../controllers/adminController.js';

const router = express.Router();

// Login de administrador (sin autenticación, para obtener token)
router.post('/login', adminLogin);

// Resto de rutas requieren autenticación y rol admin
router.get('/stats', authMiddleware, adminMiddleware, getStats);
router.get('/dashboard/stats', authMiddleware, adminMiddleware, getDashboardStats);
router.get('/professionals/pending', authMiddleware, adminMiddleware, getPendingPros);
router.post('/professionals/:id/approve', authMiddleware, adminMiddleware, approveProfessional);
router.post('/professionals/:id/reject', authMiddleware, adminMiddleware, rejectProfessional);
router.get('/users', authMiddleware, adminMiddleware, getUsers);
router.post('/users/:id/block', authMiddleware, adminMiddleware, blockUser);
router.post('/users/:id/unblock', authMiddleware, adminMiddleware, unblockUser);
router.get('/reservations', authMiddleware, adminMiddleware, getAllReservations);
router.get('/categories', authMiddleware, adminMiddleware, getCategories);
router.post('/categories', authMiddleware, adminMiddleware, createCategory);
router.put('/categories/:id', authMiddleware, adminMiddleware, updateCategory);
router.delete('/categories/:id', authMiddleware, adminMiddleware, deleteCategory);

export default router;
