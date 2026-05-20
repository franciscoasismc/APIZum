<template>
  <!-- Bloque: vista de búsqueda de usuarios (admin) -->
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="trans-list">
        <h2 class="trans-list__header">USUARIOS</h2>

        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <!-- Elemento: barra de búsqueda por username -->
        <div class="filter-bar">
          <input
            v-model="busqueda"
            type="text"
            placeholder="Buscar por username (9 dígitos)"
            class="filter-bar__input"
            @keyup.enter="buscarUsuario"
          />
          <button class="filter-bar__btn" @click="buscarUsuario">BUSCAR</button>
        </div>

        <!-- Elemento: resultado de la búsqueda -->
        <div
          v-if="usuario"
          class="trans-list__item"
          @click="$router.push(`/admin/usuarios/${usuario.username}`)"
        >
          <div>
            <div>{{ usuario.username }}</div>
            <div class="trans-list__item-sub">{{ usuario.nombre }} {{ usuario.apellidos }}</div>
          </div>
          <div>
            <div>{{ usuario.email }}</div>
            <div class="trans-list__item-sub">{{ usuario.rol }}</div>
          </div>
        </div>

        <!-- Elemento: estado vacío tras búsqueda -->
        <div v-if="!usuario && buscado" class="trans-list__empty">
          No se encontró ningún usuario con ese username.
        </div>
      </div>
    </div>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getUsuario } from '../../api'

const busqueda = ref('')
const usuario  = ref(null)
const error    = ref('')
const buscado  = ref(false)

async function buscarUsuario() {
  if (!busqueda.value.trim()) return
  error.value   = ''
  usuario.value = null
  buscado.value = false
  try {
    const res = await getUsuario(busqueda.value.trim())
    usuario.value = res.data
  } catch {
    buscado.value = true
  }
}
</script>
