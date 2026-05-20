<template>
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="card">
        <h2 class="card__header">CUENTA</h2>
        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <template v-if="!editando">
          <div class="card__info"><span>NÚMERO DE CUENTA</span>{{ cuenta.numCuenta }}</div>
          <div class="card__info"><span>SALDO</span>{{ cuenta.saldo }} €</div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="editando = true">ACTUALIZAR SALDO</button>
          </div>
        </template>

        <template v-else>
          <div class="card__field">
            <label class="card__label">NUEVO SALDO (€)</label>
            <input v-model.number="nuevoSaldo" type="number" min="0" step="0.01" class="card__input" />
          </div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="handleActualizar" :disabled="loading">
              {{ loading ? '...' : 'ACEPTAR' }}
            </button>
            <button class="card__btn card__btn--cancel" @click="editando = false">CANCELAR</button>
          </div>
        </template>
      </div>
    </div>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getCuenta, updateCuenta } from '../../api'

const loading    = ref(false)
const error      = ref('')
const success    = ref('')
const editando   = ref(false)
const cuenta     = ref({})
const nuevoSaldo = ref(0)

onMounted(async () => {
  try {
    const res = await getCuenta()
    cuenta.value = res.data
    nuevoSaldo.value = res.data.saldo
  } catch {
    error.value = 'Error al cargar la cuenta'
  }
})

async function handleActualizar() {
  error.value = ''
  success.value = ''
  loading.value = true
  try {
    const res = await updateCuenta(nuevoSaldo.value)
    cuenta.value.saldo = res.data.saldo
    editando.value = false
    success.value = 'Saldo actualizado correctamente'
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al actualizar'
  } finally {
    loading.value = false
  }
}
</script>
