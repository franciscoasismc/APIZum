<template>
  <header>
    <nav class="navbar" role="navigation" aria-label="Navegación principal">
      <!-- Logo -->
      <RouterLink to="/" class="navbar__logo" aria-label="APIzum - Inicio">
        <img src="/images/APIzum.webp" alt="APIzum" />
        <span class="navbar__logo-text">APIzum</span>
      </RouterLink>

      <div class="navbar__right">
        <!-- Botones de navegación (desktop) -->
        <div :class="['navbar__actions', { 'navbar__actions--open': menuOpen }]">
          <!-- Público -->
          <template v-if="!auth.isAuthenticated">
            <RouterLink to="/registro">
              <button class="navbar__btn">REGISTRO</button>
            </RouterLink>
            <RouterLink to="/login">
              <button class="navbar__btn">INICIAR SESIÓN</button>
            </RouterLink>
          </template>

          <!-- Usuario normal -->
          <template v-else-if="!auth.isAdmin">
            <RouterLink to="/perfil">
              <button :class="['navbar__btn', isActive('/perfil') && 'navbar__btn--active']">PERFIL</button>
            </RouterLink>
            <RouterLink to="/cuenta">
              <button :class="['navbar__btn', isActive('/cuenta') && 'navbar__btn--active']">CUENTA</button>
            </RouterLink>
            <RouterLink to="/transacciones">
              <button :class="['navbar__btn', isActive('/transacciones') && 'navbar__btn--active']">TRANSACCIONES</button>
            </RouterLink>
            <RouterLink to="/contactos">
              <button :class="['navbar__btn', isActive('/contactos') && 'navbar__btn--active']">CONTACTOS</button>
            </RouterLink>
            <RouterLink to="/solicitudes">
              <button :class="['navbar__btn', isActive('/solicitudes') && 'navbar__btn--active']">SOLICITUDES</button>
            </RouterLink>
            <RouterLink to="/notificaciones" class="navbar__notif-link">
              <button :class="['navbar__btn', isActive('/notificaciones') && 'navbar__btn--active']">
                🔔
                <span v-if="noLeidas > 0" class="navbar__badge">{{ noLeidas > 9 ? '9+' : noLeidas }}</span>
              </button>
            </RouterLink>
            <button class="navbar__btn" @click="cerrarSesion">CERRAR SESIÓN</button>
          </template>

          <!-- Admin -->
          <template v-else>
            <RouterLink to="/admin/usuarios">
              <button :class="['navbar__btn', isActive('/admin/usuarios') && 'navbar__btn--active']">USUARIOS</button>
            </RouterLink>
            <RouterLink to="/admin/transacciones">
              <button :class="['navbar__btn', isActive('/admin/transacciones') && 'navbar__btn--active']">TRANSACCIONES</button>
            </RouterLink>
            <button class="navbar__btn" @click="cerrarSesion">CERRAR SESIÓN</button>
          </template>
        </div>

        <!-- Toggle dark mode -->
        <button
          class="navbar__theme-btn"
          @click="toggleTheme"
          :aria-label="isDark ? 'Activar modo claro' : 'Activar modo oscuro'"
          :title="isDark ? 'Modo claro' : 'Modo oscuro'"
        >
          {{ isDark ? '☀️' : '🌙' }}
        </button>

        <!-- Hamburguesa (mobile) -->
        <button
          class="navbar__hamburger"
          @click="menuOpen = !menuOpen"
          :aria-expanded="menuOpen.toString()"
          aria-label="Menú"
        >
          <span></span><span></span><span></span>
        </button>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authStore as auth } from '../stores/auth'
import { getNoLeidas } from '../api'

const route    = useRoute()
const router   = useRouter()
const menuOpen = ref(false)
const isDark   = ref(false)
const noLeidas = ref(0)

onMounted(() => {
  const saved = localStorage.getItem('theme')
  if (saved === 'dark') {
    isDark.value = true
    document.documentElement.setAttribute('data-theme', 'dark')
  } else {
    document.documentElement.removeAttribute('data-theme')
  }
  cargarNoLeidas()
})

// Recarga badge al cambiar de ruta
watch(() => route.path, cargarNoLeidas)

async function cargarNoLeidas() {
  if (!auth.isAuthenticated || auth.isAdmin) return
  try {
    const res  = await getNoLeidas()
    noLeidas.value = res.data ?? 0
  } catch {
    // silencioso
  }
}

function toggleTheme() {
  isDark.value = !isDark.value
  const theme = isDark.value ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem('theme', theme)
}

function isActive(path) { return route.path.startsWith(path) }

function cerrarSesion() {
  auth.logout()
  router.push('/')
}
</script>
