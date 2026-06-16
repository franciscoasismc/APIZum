<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="trans-list">
        <div class="trans-list__top">
          <h1 class="trans-list__header">NOTIFICACIONES</h1>
          <button
            v-if="lista.some(n => !n.leida)"
            class="trans-list__btn-pdf"
            @click="handleMarcarTodas"
          >
            ✓ Marcar todas
          </button>
        </div>

        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div v-if="lista.length === 0" class="trans-list__empty">
          No tienes notificaciones.
        </div>

        <div
          v-for="n in lista"
          :key="n.id"
          :class="['trans-list__item', 'trans-list__item--static', !n.leida && 'trans-list__item--recibida']"
        >
          <span class="trans-list__dir" style="min-width:1.5rem">
            {{ iconoTipo(n.tipo) }}
          </span>
          <div class="trans-list__item-body">
            <div>
              <div class="trans-list__item-cuenta">{{ n.mensaje }}</div>
              <div class="trans-list__item-sub">{{ formatFecha(n.fecha) }}</div>
            </div>
            <span v-if="!n.leida" class="badge" style="background:var(--color-primary);color:#fff">Nuevo</span>
          </div>
        </div>

        <!-- Paginación -->
        <div class="pagination" v-if="totalPages > 1">
          <button class="pagination__btn" :disabled="page === 0" @click="cambiarPagina(page - 1)">← Anterior</button>
          <span class="pagination__info">{{ page + 1 }} / {{ totalPages }}</span>
          <button class="pagination__btn" :disabled="page >= totalPages - 1" @click="cambiarPagina(page + 1)">Siguiente →</button>
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
import { getNotificaciones, marcarTodasLeidas } from '../../api'

const loading    = ref(false)
const error      = ref('')
const lista      = ref([])
const page       = ref(0)
const totalPages = ref(1)
const PAGE_SIZE  = 20

async function cargar() {
  error.value = ''
  try {
    const res = await getNotificaciones({ page: page.value, size: PAGE_SIZE, sort: 'fecha,desc' })
    lista.value      = res.data.content || []
    totalPages.value = res.data.totalPages || 1
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al cargar notificaciones'
  }
}

function cambiarPagina(p) {
  page.value = p
  cargar()
}

async function handleMarcarTodas() {
  try {
    await marcarTodasLeidas()
    await cargar()
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al marcar notificaciones'
  }
}

function iconoTipo(tipo) {
  if (!tipo) return '🔔'
  if (tipo.includes('ENVIADA'))   return '↑'
  if (tipo.includes('RECIBIDA'))  return '↓'
  if (tipo.includes('SOLICITUD')) return '📋'
  return '🔔'
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleString('es-ES', {
    day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}

onMounted(cargar)
</script>
