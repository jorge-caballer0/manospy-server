const { supabase } = require('../config/supabase.js');

// Calificaciones de soporte
export async function rateSupport({ user_id, agent_id, rating, tags, comment }) {
  return supabase.from('CalificacionesSoporte').insert([{ user_id, agent_id, rating, tags, comment }]);
}

// Calificaciones entre usuarios
export async function rateUser({ from_user_id, to_user_id, rating, tags, comment }) {
  return supabase.from('CalificacionesUsuario').insert([{ from_user_id, to_user_id, rating, tags, comment }]);
}
