import { supabase } from '../config/supabase.js';

// CRUD para direcciones
export async function addDireccion({ user_id, direccion, ciudad, estado, codigo_postal }) {
  return supabase.from('Direcciones').insert([{ user_id, direccion, ciudad, estado, codigo_postal }]);
}

export async function getDirecciones(user_id) {
  return supabase.from('Direcciones').select('*').eq('user_id', user_id);
}

export async function updateDireccion(id, data) {
  return supabase.from('Direcciones').update(data).eq('id', id);
}

export async function deleteDireccion(id) {
  return supabase.from('Direcciones').delete().eq('id', id);
}
