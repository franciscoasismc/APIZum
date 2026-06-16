<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="card">
        <h1 class="card__header">CUENTA</h1>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div class="card__info"><span>NÚMERO DE CUENTA</span>{{ cuenta.numCuenta }}</div>
        <div class="card__info"><span>SALDO</span>{{ cuenta.saldo }} €</div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="$router.push('/transacciones/nueva')">
            NUEVA TRANSACCIÓN
          </button>
          <button class="card__btn card__btn--cancel" @click="$router.push('/transacciones')">
            VER HISTORIAL
          </button>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getCuenta } from '../../api'

const error  = ref('')
const cuenta = ref({})

onMounted(async () => {
  try {
    const res = await getCuenta()
    cuenta.value = res.data
  } catch {
    error.value = 'Error al cargar la cuenta'
  }
})
</script>
