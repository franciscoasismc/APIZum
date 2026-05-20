/**
 * stores/auth.js — Estado global de autenticación
 * =================================================
 * Almacena el token JWT, el username y el rol del usuario en sesión.
 * Persiste los datos en localStorage para que la sesión sobreviva
 * a recargas de página. Al iniciar la app, recupera automáticamente
 * los datos guardados si existen.
 */

import { reactive } from 'vue'

/**
 * Decodifica el payload de un JWT sin librerías externas.
 * El payload es la segunda parte del token (separada por puntos),
 * codificada en Base64url. Se convierte a Base64 estándar y se parsea
 * como JSON para extraer los claims (roles, username, expiración, etc.).
 *
 * @param {string} token — JWT completo
 * @returns {object} — Payload decodificado, o {} si falla
 */
function parseJwt(token) {
  try {
    // Convierte Base64url → Base64 estándar y decodifica
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    return JSON.parse(atob(base64))
  } catch {
    return {}
  }
}

/* Store reactivo de autenticación.
  Todos los campos y métodos son accesibles desde cualquier componente. */
export const authStore = reactive({

  // Estado
  /** JWT devuelto por el servidor al hacer login */
  token:    localStorage.getItem('token')    || null,
  /** Username (ID numérico de 9 dígitos) del usuario en sesión */
  username: localStorage.getItem('username') || null,
  /** Rol del usuario: 'USER' o 'ADMIN' */
  rol:      localStorage.getItem('rol')      || null,

  // Métodos

  /**
   * Guarda la sesión tras un login exitoso.
   * Extrae el rol del payload JWT y lo persiste en localStorage.
   *
   * @param {string} token    — JWT recibido del servidor
   * @param {string} username — Username del usuario autenticado
   */
  login(token, username) {
    const payload = parseJwt(token)
    const rol = payload.roles?.[0] || 'USER' // El servidor guarda los roles como array
    this.token    = token
    this.username = username
    this.rol      = rol
    localStorage.setItem('token',    token)
    localStorage.setItem('username', username)
    localStorage.setItem('rol',      rol)
  },

  /* Cierra la sesión limpiando el estado y el localStorage. */
  logout() {
    this.token    = null
    this.username = null
    this.rol      = null
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('rol')
  },

  // Getters (propiedades calculadas)

  /** true si hay un token activo en sesión */
  get isAuthenticated() {
    return !!this.token
  },

  /** true si el usuario autenticado tiene rol ADMIN */
  get isAdmin() {
    return this.rol === 'ADMIN'
  }
})
