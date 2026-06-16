<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="trans-list">
        <div class="trans-list__top">
          <h1 class="trans-list__header">SOLICITUDES</h1>
          <button class="trans-list__btn-pdf" @click="mostrarForm = !mostrarForm">
            {{ mostrarForm ? '✕ Cancelar' : '+ Nueva' }}
          </button>
        </div>

        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <!-- Formulario nueva solicitud -->
        <div v-if="mostrarForm" class="card" style="margin-bottom:1.5rem">
          <div class="card__field">
            <label class="card__label">EMAIL DEL DEUDOR</label>
            <input v-model="solEmail" type="email" class="card__input" placeholder="persona@ejemplo.com" />
          </div>
          <div class="card__field">
            <label class="card__label">CANTIDAD (€)</label>
            <input v-model.number="solCantidad" type="number" min="0.01" step="0.01" class="card__input" />
          </div>
          <div class="card__field">
            <label class="card__label">DESCRIPCIÓN (opcional)</label>
            <input v-model="solDescripcion" type="text" class="card__input" placeholder="Cena del sábado..." />
          </div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="handleCrear" :disabled="loading">
              {{ loading ? '...' : 'SOLICITAR' }}
            </button>
          </div>
        </div>

        <!-- Tabs -->
        <div class="filter-bar" style="margin-bottom:1rem">
          <button :class="['filter-bar__pill', tabActivo === 'enviadas'  && 'filter-bar__pill--active']" @click="cambiarTab('enviadas')">Mis solicitudes</button>
          <button :class="['filter-bar__pill', tabActivo === 'recibidas' && 'filter-bar__pill--active']" @click="cambiarTab('recibidas')">Recibidas</button>
        </div>

        <div v-if="lista.length === 0" class="trans-list__empty">
          No hay solicitudes que mostrar.
        </div>

        <div
          v-for="s in lista"
          :key="s.id"
          :class="['trans-list__item', 'trans-list__item--static',
                   s.estado === 'ACEPTADA' ? 'trans-list__item--recibida' :
                   s.estado === 'RECHAZADA' || s.estado === 'CANCELADA' ? 'trans-list__item--enviada' : '']"
        >
          <div class="trans-list__item-body">
            <div>
              <div class="trans-list__item-cuenta">
                <span class="badge">{{ s.estado }}</span>
                {{ tabActivo === 'enviadas' ? 'Solicitado a: ' : 'De: ' }}
                {{ tabActivo === 'enviadas' ? s.cuentaDestino?.numCuenta : s.cuentaSolicitante?.numCuenta }}
              </div>
              <div class="trans-list__item-sub">{{ s.descripcion || '—' }}</div>
              <div class="trans-list__item-sub">{{ formatFecha(s.fecha) }}</div>
            </div>
            <div style="text-align:right">
              <strong>{{ s.cantidad }} €</strong>
              <div v-if="tabActivo === 'recibidas' && s.estado === 'PENDIENTE'" style="display:flex;gap:.4rem;margin-top:.4rem">
                <button class="card__btn card__btn--accept" style="padding:.3rem .6rem;font-size:.8rem" @click="handleResponder(s, true)">✓ Aceptar</button>
                <button class="card__btn card__btn--cancel" style="padding:.3rem .6rem;font-size:.8rem" @click="handleResponder(s, false)">✕ Rechazar</button>
              </div>
              <div v-if="tabActivo === 'enviadas' && s.estado === 'PENDIENTE'" style="margin-top:.4rem">
                <button class="card__btn card__btn--cancel" style="padding:.3rem .6rem;font-size:.8rem" @click="handleCancelar(s)">Cancelar</button>
              </div>
            </div>
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
import { getSolicitudes, getSolicitudesPendientes, crearSolicitud, responderSolicitud, cancelarSolicitud, buscarPorEmail } from '../../api'

const loading     = ref(false)
const error       = ref('')
const success     = ref('')
const lista       = ref([])
const tabActivo   = ref('enviadas')
const page        = ref(0)
const totalPages  = ref(1)
const mostrarForm = ref(false)
const solEmail    = ref('')
const solCantidad = ref(null)
const solDescripcion = ref('')
const PAGE_SIZE = 10

async function cargar() {
  error.value = ''
  try {
    const params = { page: page.value, size: PAGE_SIZE, sort: 'fecha,desc' }
    const res = tabActivo.value === 'recibidas'
      ? await getSolicitudesPendientes(params)
      : await getSolicitudes(params)
    lista.value      = res.data.content || []
    totalPages.value = res.data.totalPages || 1
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al cargar solicitudes'
  }
}

function cambiarTab(tab) {
  tabActivo.value = tab
  page.value = 0
  cargar()
}

function cambiarPagina(p) {
  page.value = p
  cargar()
}

async function handleCrear() {
  const email = solEmail.value.trim()
  if (!email || !(solCantidad.value > 0)) return
  loading.value = true
  error.value   = ''
  success.value = ''
  try {
    const res  = await buscarPorEmail(email)
    const user = res.data
    await crearSolicitud({
      numCuentaDestino: user.numCuenta,
      cantidad: solCantidad.value,
      descripcion: solDescripcion.value.trim() || null
    })
    success.value    = 'Solicitud enviada'
    mostrarForm.value = false
    solEmail.value    = ''
    solCantidad.value = null
    solDescripcion.value = ''
    await cargar()
  } catch (e) {
    error.value = e.response?.status === 404
      ? 'Usuario no encontrado'
      : e.response?.data?.error || 'Error al crear solicitud'
  } finally {
    loading.value = false
  }
}

async function handleResponder(s, aceptar) {
  try {
    await responderSolicitud(s.id, aceptar)
    success.value = aceptar ? 'Solicitud aceptada' : 'Solicitud rechazada'
    await cargar()
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al responder solicitud'
  }
}

async function handleCancelar(s) {
  if (!confirm('¿Cancelar esta solicitud?')) return
  try {
    await cancelarSolicitud(s.id)
    success.value = 'Solicitud cancelada'
    await cargar()
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al cancelar solicitud'
  }
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleString('es-ES', {
    day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}

onMounted(cargar)
</script>
