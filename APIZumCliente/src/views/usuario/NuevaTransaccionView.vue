<template>
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="card">
        <h2 class="card__header">NUEVA TRANSACCIÓN</h2>
        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <div class="card__field">
          <label class="card__label">CUENTA DESTINO</label>
          <input v-model="form.numCuentaDestino" type="text" class="card__input" placeholder="ES1234567890" />
        </div>
        <div class="card__field">
          <label class="card__label">CANTIDAD (€)</label>
          <input v-model.number="form.cantidad" type="number" min="0.01" step="0.01" class="card__input" />
        </div>
        <div class="card__field">
          <label class="card__label">DESCRIPCIÓN</label>
          <input v-model="form.descripcion" type="text" class="card__input" placeholder="Opcional" />
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleEnviar" :disabled="loading">
            {{ loading ? '...' : 'ACEPTAR' }}
          </button>
          <button class="card__btn card__btn--cancel" @click="$router.push('/transacciones')">CANCELAR</button>
        </div>
      </div>
    </div>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { crearTransaccion } from '../../api'

const router  = useRouter()
const loading = ref(false)
const error   = ref('')
const success = ref('')
const form    = ref({ numCuentaDestino: '', cantidad: null, descripcion: '' })

async function handleEnviar() {
  error.value = ''
  success.value = ''
  loading.value = true
  try {
    await crearTransaccion(form.value)
    success.value = 'Transferencia realizada correctamente'
    setTimeout(() => router.push('/transacciones'), 1500)
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al realizar la transferencia'
  } finally {
    loading.value = false
  }
}
</script>
