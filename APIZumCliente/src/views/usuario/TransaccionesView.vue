<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="trans-list">
        <div class="trans-list__top">
          <h1 class="trans-list__header">MIS TRANSACCIONES</h1>
          <button class="trans-list__btn-pdf" @click="exportarPDF" :disabled="transacciones.length === 0">
            ⬇ PDF
          </button>
        </div>

        <div v-if="error" class="alert alert--error" role="alert">{{ error }}</div>

        <!-- Filtros -->
        <div class="filter-bar">
          <button
            v-for="f in filtros" :key="f.value"
            :class="['filter-bar__pill', { 'filter-bar__pill--active': filtroActivo === f.value }]"
            @click="filtroActivo = f.value"
          >{{ f.label }}</button>
        </div>

        <div v-if="transaccionesFiltradas.length === 0 && !loading" class="trans-list__empty">
          No hay transacciones que mostrar.
        </div>

        <div
          v-for="t in transaccionesFiltradas"
          :key="t.idTransaccion"
          :class="['trans-list__item', 'trans-list__item--static',
                   esEnviada(t) ? 'trans-list__item--enviada' : 'trans-list__item--recibida']"
        >
          <span :class="['trans-list__dir', esEnviada(t) ? 'trans-list__dir--enviada' : 'trans-list__dir--recibida']">
            {{ esEnviada(t) ? '↑' : '↓' }}
          </span>
          <div class="trans-list__item-body">
            <div>
              <div class="trans-list__item-cuenta">
                {{ esEnviada(t) ? 'A: ' : 'De: ' }}
                {{ esEnviada(t) ? t.cuentaDestino?.numCuenta : t.cuentaOrigen?.numCuenta }}
              </div>
              <div class="trans-list__item-sub">{{ t.descripcion || '—' }}</div>
            </div>
            <div>
              <strong :class="esEnviada(t) ? 'amount--enviada' : 'amount--recibida'">
                {{ esEnviada(t) ? '-' : '+' }}{{ t.cantidad }} €
              </strong>
              <div class="trans-list__item-sub">{{ formatFecha(t.fecha) }}</div>
            </div>
          </div>
        </div>

        <!-- Paginación -->
        <div class="pagination" v-if="totalPages > 1">
          <button class="pagination__btn" :disabled="page === 0" @click="cambiarPagina(page - 1)">
            ← Anterior
          </button>
          <span class="pagination__info">{{ page + 1 }} / {{ totalPages }}</span>
          <button class="pagination__btn" :disabled="page >= totalPages - 1" @click="cambiarPagina(page + 1)">
            Siguiente →
          </button>
        </div>

        <button class="trans-list__btn" @click="$router.push('/transacciones/nueva')">
          NUEVA TRANSACCIÓN
        </button>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getTransacciones, getCuenta } from '../../api'

const loading       = ref(false)
const error         = ref('')
const transacciones = ref([])
const miCuenta      = ref('')
const page          = ref(0)
const totalPages    = ref(1)
const filtroActivo  = ref('todos')
const PAGE_SIZE     = 10

const filtros = [
  { value: 'todos',     label: 'Todos' },
  { value: 'enviadas',  label: '↑ Enviadas' },
  { value: 'recibidas', label: '↓ Recibidas' },
]

function esEnviada(t) {
  return t.cuentaOrigen?.numCuenta === miCuenta.value
}

const transaccionesFiltradas = computed(() => {
  if (filtroActivo.value === 'enviadas')  return transacciones.value.filter(t =>  esEnviada(t))
  if (filtroActivo.value === 'recibidas') return transacciones.value.filter(t => !esEnviada(t))
  return transacciones.value
})

async function cargar() {
  loading.value = true
  error.value   = ''
  try {
    const res = await getTransacciones({ page: page.value, size: PAGE_SIZE, sort: 'fecha,desc' })
    transacciones.value = res.data.content || []
    totalPages.value    = res.data.totalPages || 1
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al cargar las transacciones'
  } finally {
    loading.value = false
  }
}

async function cargarCuenta() {
  try {
    const res = await getCuenta()
    miCuenta.value = res.data.numCuenta || ''
  } catch {
    // silencioso
  }
}

function cambiarPagina(p) {
  page.value = p
  cargar()
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleString('es-ES', {
    day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}

async function exportarPDF() {
  const script = document.createElement('script')
  script.src = 'https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js'
  document.head.appendChild(script)
  await new Promise(resolve => { script.onload = resolve })

  const { jsPDF } = window.jspdf
  const doc = new jsPDF()

  doc.setFontSize(16)
  doc.text('Historial de transacciones — APIzum', 14, 20)
  doc.setFontSize(10)
  doc.text(`Cuenta: ${miCuenta.value}`, 14, 29)
  doc.text(`Generado: ${new Date().toLocaleString('es-ES')}`, 14, 35)

  let y = 46
  doc.setFontSize(8)
  doc.setFont(undefined, 'bold')
  doc.text('Fecha',       14,  y)
  doc.text('Tipo',        55,  y)
  doc.text('Cuenta',      78,  y)
  doc.text('Descripción', 130, y)
  doc.text('Importe',     178, y)
  doc.setFont(undefined, 'normal')
  y += 5
  doc.line(14, y, 196, y)
  y += 4

  transacciones.value.forEach(t => {
    if (y > 282) { doc.addPage(); y = 20 }
    const enviada = esEnviada(t)
    const tipo    = enviada ? 'ENVIADA' : 'RECIBIDA'
    const cuenta  = (enviada ? t.cuentaDestino?.numCuenta : t.cuentaOrigen?.numCuenta) || ''
    const signo   = enviada ? '-' : '+'
    doc.text(formatFecha(t.fecha).slice(0, 16),   14,  y)
    doc.text(tipo,                                 55,  y)
    doc.text(cuenta.slice(0, 22),                  78,  y)
    doc.text((t.descripcion || '—').slice(0, 20), 130,  y)
    doc.text(`${signo}${t.cantidad} €`,            178,  y)
    y += 6
  })

  doc.save(`transacciones-${miCuenta.value}-${Date.now()}.pdf`)
}

onMounted(async () => {
  await cargarCuenta()
  await cargar()
})
</script>
