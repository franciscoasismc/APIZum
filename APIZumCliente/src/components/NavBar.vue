<template>
  <nav class="navbar">
    <RouterLink to="/" class="navbar__logo">
      <img src="/logo.svg" alt="ApiBank" />
    </RouterLink>

    <!-- Mobile hamburger -->
    <button class="navbar__hamburger" @click="menuOpen = !menuOpen" aria-label="Menú">
      <span></span><span></span><span></span>
    </button>

    <!-- Nav links -->
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
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authStore as auth } from '../stores/auth'

const route  = useRoute()
const router = useRouter()
const menuOpen = ref(false)

function isActive(path) { return route.path.startsWith(path) }

function cerrarSesion() {
  auth.logout()
  router.push('/')
}
</script>
