/**
 * api/index.js — Capa de comunicación con el backend
 * =====================================================
 * Centraliza todas las llamadas HTTP a la API REST.
 * Usa axios que Vite redirige al backend.
 *
 * El interceptor adjunta automáticamente el token JWT en la cabecera
 * Authorization de cada petición para las rutas protegidas.
 */

import axios from 'axios'

/* Instancia de axios configurada con la URL base de la API.
  Todas las funciones exportadas usan esta instancia. */
const api = axios.create({ baseURL: '/api' })

/* Interceptor de peticiones: añade el token JWT al header Authorization
  si existe en el localStorage. El servidor lo usa para identificar
  y autorizar al usuario en cada petición. */
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})


// AUTH — rutas públicas

/* POST /auth/login — Autentica al usuario y devuelve un JWT */
export const login          = (data)          => api.post('/auth/login', data)

/* POST /auth/registro — Registra un nuevo usuario (sin cuenta) */
export const registro       = (data)          => api.post('/auth/registro', data)

/* POST /auth/registro/cuenta — Asocia una cuenta bancaria al usuario recién registrado */
export const registroCuenta = (data)          => api.post('/auth/registro/cuenta', data)


// USUARIO — rutas de usuario autenticado (USER)

/* GET /usuarios/ — Devuelve el perfil del usuario en sesión */
export const getPerfil      = ()              => api.get('/usuarios/')

/* PUT /usuarios/ — Actualiza los datos del usuario en sesión */
export const updatePerfil   = (data)          => api.put('/usuarios/', data)


// CUENTA — rutas de usuario autenticado (USER)

/* GET /cuentas/ — Devuelve la cuenta del usuario en sesión */
export const getCuenta      = ()              => api.get('/cuentas/')

/* PUT /cuentas/ — Actualiza el saldo de la cuenta del usuario en sesión */
export const updateCuenta   = (saldo)         => api.put('/cuentas/', { saldo })


// TRANSACCIONES — rutas de usuario autenticado (USER)

/* GET /transacciones/ — Devuelve el historial de transacciones del usuario (paginado) */
export const getTransacciones  = (params)     => api.get('/transacciones/', { params })

/* POST /transacciones/ — Realiza una nueva transferencia desde la cuenta propia */
export const crearTransaccion  = (data)       => api.post('/transacciones/', data)


// ADMIN — Usuarios

/* GET /usuarios/{username} — Devuelve el perfil de cualquier usuario */
export const getUsuario     = (username)      => api.get(`/usuarios/${username}`)

/* PUT /usuarios/{username} — Actualiza cualquier usuario (incluyendo rol) */
export const updateUsuario  = (username, data)=> api.put(`/usuarios/${username}`, data)

/* DELETE /usuarios/{username} — Elimina un usuario y su cuenta asociada */
export const deleteUsuario  = (username)      => api.delete(`/usuarios/${username}`)


// ADMIN — Cuentas

/* GET /cuentas/{numCuenta} — Devuelve cualquier cuenta por número */
export const getCuentaAdmin = (numCuenta)     => api.get(`/cuentas/${numCuenta}`)


// ADMIN — Transacciones

/* GET /transacciones/usuario/{username} — Historial de transacciones de un usuario */
export const getTransPorUsername = (username, p) => api.get(`/transacciones/usuario/${username}`, { params: p })

/* GET /transacciones/cuenta/{numCuenta} — Historial de transacciones de una cuenta */
export const getTransPorCuenta   = (numCuenta, p)=> api.get(`/transacciones/cuenta/${numCuenta}`, { params: p })

/* GET /transacciones/{id} — Devuelve una transacción concreta por su ID */
export const getTransaccion      = (id)          => api.get(`/transacciones/${id}`)
