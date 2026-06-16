/**
 * router/index.js — Configuración de rutas de la aplicación
 * ===========================================================
 * Define qué componente (vista) se muestra para cada URL.
 * Incluye guardias de navegación que protegen las rutas privadas:
 *   - meta.requiresAuth  → solo usuarios autenticados (USER o ADMIN)
 *   - meta.requiresAdmin → solo usuarios con rol ADMIN
 * Si el usuario no cumple los requisitos, se redirige al login.
 */

import { createRouter, createWebHistory } from 'vue-router'
import { authStore } from '../stores/auth'

// Vistas públicas
import InicioView            from '../views/InicioView.vue'
import LoginView             from '../views/LoginView.vue'
import RegistroView          from '../views/RegistroView.vue'

// Vistas footer (páginas estáticas)
import SobreNosotrosView     from '../views/SobreNosotrosView.vue'
import ContactoView          from '../views/ContactoView.vue'
import AvisoLegalView        from '../views/AvisoLegalView.vue'
import PrivacidadView        from '../views/PrivacidadView.vue'

// Vistas de usuario autenticado (rol USER)
import PerfilView            from '../views/usuario/PerfilView.vue'
import CuentaView            from '../views/usuario/CuentaView.vue'
import TransaccionesView     from '../views/usuario/TransaccionesView.vue'
import NuevaTransaccionView  from '../views/usuario/NuevaTransaccionView.vue'
import ContactosView         from '../views/usuario/ContactosView.vue'
import SolicitudesView       from '../views/usuario/SolicitudesView.vue'
import NotificacionesView    from '../views/usuario/NotificacionesView.vue'

// Vistas de administrador (rol ADMIN)
import AdminUsuariosView     from '../views/admin/UsuariosView.vue'
import AdminUsuarioDetalle   from '../views/admin/UsuarioDetalleView.vue'
import AdminCuentaDetalle    from '../views/admin/CuentaDetalleView.vue'
import AdminTransacciones    from '../views/admin/TransaccionesView.vue'
import AdminTransDetalle     from '../views/admin/TransaccionDetalleView.vue'

const routes = [
  // Rutas públicas
  { path: '/',                         component: InicioView },
  { path: '/login',                    component: LoginView },
  { path: '/registro',                 component: RegistroView },

  // Rutas páginas estáticas (footer)
  { path: '/sobre-nosotros',           component: SobreNosotrosView },
  { path: '/contacto',                 component: ContactoView },
  { path: '/aviso-legal',             component: AvisoLegalView },
  { path: '/privacidad',               component: PrivacidadView },

  // Rutas de usuario autenticado
  { path: '/perfil',                   component: PerfilView,           meta: { requiresAuth: true } },
  { path: '/cuenta',                   component: CuentaView,           meta: { requiresAuth: true } },
  { path: '/transacciones',            component: TransaccionesView,    meta: { requiresAuth: true } },
  { path: '/transacciones/nueva',      component: NuevaTransaccionView, meta: { requiresAuth: true } },
  { path: '/contactos',                component: ContactosView,        meta: { requiresAuth: true } },
  { path: '/solicitudes',              component: SolicitudesView,      meta: { requiresAuth: true } },
  { path: '/notificaciones',           component: NotificacionesView,   meta: { requiresAuth: true } },

  // Rutas de administrador
  { path: '/admin/usuarios',           component: AdminUsuariosView,    meta: { requiresAdmin: true } },
  { path: '/admin/usuarios/:username', component: AdminUsuarioDetalle,  meta: { requiresAdmin: true } },
  { path: '/admin/cuentas/:numCuenta', component: AdminCuentaDetalle,   meta: { requiresAdmin: true } },
  { path: '/admin/transacciones',      component: AdminTransacciones,   meta: { requiresAdmin: true } },
  { path: '/admin/transacciones/:id',  component: AdminTransDetalle,    meta: { requiresAdmin: true } },
]

const router = createRouter({
  history: createWebHistory(), // URLs limpias sin el símbolo #
  routes
})

/* Guardia de navegación global — se ejecuta antes de cada cambio de ruta.
 * Comprueba los meta campos de la ruta destino y redirige si es necesario.*/
router.beforeEach((to) => {
  // Ruta solo para ADMIN: redirige si no es admin
  if (to.meta.requiresAdmin && !authStore.isAdmin)        return '/login'
  // Ruta para cualquier usuario autenticado: redirige si no hay sesión
  if (to.meta.requiresAuth  && !authStore.isAuthenticated) return '/login'
})

export default router
