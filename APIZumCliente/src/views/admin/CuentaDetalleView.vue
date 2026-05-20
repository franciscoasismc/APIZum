<template>
  <!-- Bloque: detalle de cuenta (admin) -->
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="card">
        <h2 class="card__header">DATOS DE LA CUENTA</h2>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div class="card__info"><span>NÚMERO DE CUENTA</span>{{ cuenta.numCuenta }}</div>
        <div class="card__info"><span>SALDO</span>{{ cuenta.saldo }} €</div>
        <div class="card__info">
          <span>TITULAR</span>
          <!-- Elemento: enlace clicable al perfil del titular -->
          <span
            v-if="cuenta.usuario"
            class="link-cell"
            @click="$router.push(`/admin/usuarios/${cuenta.usuario.username}`)"
          >{{ cuenta.usuario.username }} — {{ cuenta.usuario.nombre }}</span>
        </div>

        <div class="card__actions">
          <button
            class="card__btn card__btn--accept"
            @click="$router.push(`/admin/transacciones?cuenta=${cuenta.numCuenta}`)"
          >
            VER TRANSACCIONES
          </button>
          <button class="card__btn card__btn--cancel" @click="$router.back()">VOLVER</button>
        </div>
      </div>
    </div>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getCuentaAdmin } from '../../api'

const route  = useRoute()
const error  = ref('')
const cuenta = ref({})

onMounted(async () => {
  try {
    const res = await getCuentaAdmin(route.params.numCuenta)
    cuenta.value = res.data
  } catch {
    error.value = 'Error al cargar la cuenta'
  }
})
</script>
