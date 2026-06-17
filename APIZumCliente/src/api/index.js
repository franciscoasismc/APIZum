/**
 * api/index.js — Capa de comunicación con el backend
 * =====================================================
 */

import axios from 'axios'
import { authStore } from '../stores/auth'

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/api' })

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Si el backend devuelve 401 (token inválido o expirado), cerrar sesión y redirigir al login
api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      authStore.logout()
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)


// AUTH

export const login          = (data)    => api.post('/auth/login', data)
export const registro       = (data)    => api.post('/auth/registro', data)
// Paso 2 del registro: requiere JWT devuelto en el paso 1
export const registroCuenta = (data)    => api.post('/auth/registro/cuenta', data)


// USUARIO (USER)

export const getPerfil      = ()              => api.get('/usuarios/')
export const updatePerfil   = (data)          => api.put('/usuarios/', data)
// Buscar destinatario por email (para nueva transacción / contactos)
export const buscarPorEmail = (email)         => api.get('/usuarios/buscar', { params: { email } })


// CUENTA (USER)

export const getCuenta      = ()              => api.get('/cuentas/')
// PUT /cuentas/ eliminado para usuarios — saldo solo cambia vía transacciones


// TRANSACCIONES (USER)

export const getTransacciones  = (params)    => api.get('/transacciones/', { params })
export const crearTransaccion  = (data)      => api.post('/transacciones/', data)


// CONTACTOS (USER)

export const getContactos      = ()                      => api.get('/contactos')
export const getFavoritos      = ()                      => api.get('/contactos/favoritos')
export const agregarContacto   = (data)                  => api.post('/contactos', data)
export const toggleFavorito    = (id)                    => api.put(`/contactos/${id}/favorito`)
export const eliminarContacto  = (id)                    => api.delete(`/contactos/${id}`)


// SOLICITUDES (USER)

export const getSolicitudes           = (params) => api.get('/solicitudes', { params })
export const getSolicitudesPendientes = (params) => api.get('/solicitudes/pendientes', { params })
export const crearSolicitud           = (data)   => api.post('/solicitudes', data)
export const responderSolicitud       = (id, aceptar) => api.put(`/solicitudes/${id}/responder`, { aceptar })
export const cancelarSolicitud        = (id)     => api.put(`/solicitudes/${id}/cancelar`)


// NOTIFICACIONES (USER)

export const getNotificaciones  = (params) => api.get('/notificaciones', { params })
export const getNoLeidas        = ()       => api.get('/notificaciones/no-leidas')
export const marcarTodasLeidas  = ()       => api.put('/notificaciones/leer-todas')


// ADMIN — Usuarios

export const getUsuarios    = (params)         => api.get('/usuarios', { params })
export const getUsuario     = (username)        => api.get(`/usuarios/${username}`)
export const updateUsuario  = (username, data)  => api.put(`/usuarios/${username}`, data)
export const deleteUsuario  = (username)        => api.delete(`/usuarios/${username}`)


// ADMIN — Cuentas

export const getCuentaAdmin = (numCuenta)       => api.get(`/cuentas/${numCuenta}`)
export const updateCuentaAdmin = (numCuenta, saldo) => api.put(`/cuentas/${numCuenta}`, { saldo })


// ADMIN — Transacciones

export const getTransPorUsername = (username, p) => api.get(`/transacciones/usuario/${username}`, { params: p })
export const getTransPorCuenta   = (numCuenta, p)=> api.get(`/transacciones/cuenta/${numCuenta}`, { params: p })
export const getTransaccion      = (id)          => api.get(`/transacciones/${id}`)
