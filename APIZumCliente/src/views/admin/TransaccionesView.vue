<template>
  <!-- Bloque: listado de transacciones (admin) con filtros -->
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="trans-list">
        <h1 class="trans-list__header">TRANSACCIONES</h1>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <!-- Elemento: filtros de búsqueda -->
        <div class="filter-bar">
          <input
            v-model="filtroUsername"
            type="text"
            placeholder="Username (9 dígitos)"
            class="filter-bar__input"
          />
          <input
            v-model="filtroCuenta"
            type="text"
            placeholder="Núm. cuenta (ES...)"
            class="filter-bar__input"
          />
          <button class="filter-bar__btn" @click="buscar">BUSCAR</button>
        </div>

        <!-- Elemento: estado vacío -->
        <div v-if="transacciones.length === 0 && buscado" class="trans-list__empty">
          No se encontraron transacciones.
        </div>

        <!-- Elemento: fila de transacción -->
        <div
          v-for="t in transacciones"
          :key="t.idTransaccion"
          class="trans-list__item"
          @click="$router.push(`/admin/transacciones/${t.idTransaccion}`)"
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
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getTransPorUsername, getTransPorCuenta } from '../../api'

const route          = useRoute()
const error          = ref('')
const buscado        = ref(false)
const transacciones  = ref([])
const filtroUsername = ref('')
const filtroCuenta   = ref(route.query.cuenta || '')

// Si viene desde detalle de cuenta, lanzar búsqueda automáticamente
onMounted(() => { if (filtroCuenta.value) buscar() })

async function buscar() {
  error.value        = ''
  transacciones.value = []
  buscado.value      = true
  try {
    let res
    if (filtroUsername.value.trim()) {
      res = await getTransPorUsername(filtroUsername.value.trim(), { page: 0, size: 20 })
    } else if (filtroCuenta.value.trim()) {
      res = await getTransPorCuenta(filtroCuenta.value.trim(), { page: 0, size: 20 })
    } else {
      error.value = 'Introduce un username o número de cuenta para buscar'
      return
    }
    transacciones.value = res.data.content || []
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al buscar transacciones'
  }
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
</script>
