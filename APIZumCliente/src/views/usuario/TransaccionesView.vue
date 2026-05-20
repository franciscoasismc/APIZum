<template>
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="trans-list">
        <h2 class="trans-list__header">MIS TRANSACCIONES</h2>

        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div v-if="transacciones.length === 0 && !loading" class="trans-list__empty">
          No hay transacciones aún.
        </div>

        <div
          v-for="t in transacciones"
          :key="t.idTransaccion"
          class="trans-list__item trans-list__item--static"
        >
          <div>
            <div>{{ t.cuentaOrigen?.numCuenta }} → {{ t.cuentaDestino?.numCuenta }}</div>
            <div class="trans-list__item-sub">{{ t.descripcion || '—' }}</div>
          </div>
          <div>
            <strong>{{ t.cantidad }} €</strong>
            <div class="trans-list__item-sub">{{ formatFecha(t.fecha) }}</div>
          </div>
        </div>

        <button class="trans-list__btn" @click="$router.push('/transacciones/nueva')">
          NUEVA TRANSACCIÓN
        </button>
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
import { getTransacciones } from '../../api'

const loading      = ref(false)
const error        = ref('')
const transacciones = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getTransacciones({ page: 0, size: 20 })
    transacciones.value = res.data.content || []
  } catch {
    error.value = 'Error al cargar las transacciones'
  } finally {
    loading.value = false
  }
})

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
</script>
