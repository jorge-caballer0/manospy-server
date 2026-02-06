import { supabase } from '../config/config.js';

// CRUD para chat de soporte
export async function sendSupportMessage({ user_id, agent_id, message, type }) {
  return supabase.from('ChatsSoporte').insert([{ user_id, agent_id, message, type }]);
}

export async function getSupportMessages(chatId) {
  return supabase.from('ChatsSoporte').select('*').eq('id', chatId).order('created_at');
}
