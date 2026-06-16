<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="trans-list">
        <div class="trans-list__top">
          <h1 class="trans-list__header">CONTACTOS</h1>
          <button class="trans-list__btn-pdf" @click="mostrarForm = !mostrarForm">
            {{ mostrarForm ? '✕ Cancelar' : '+ Añadir' }}
          </button>
        </div>

        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <!-- Formulario añadir contacto -->
        <div v-if="mostrarForm" class="card" style="margin-bottom:1.5rem">
          <div class="card__field">
            <label class="card__label">EMAIL DEL CONTACTO</label>
            <input v-model="nuevoEmail" type="email" class="card__input" placeholder="contacto@ejemplo.com" />
          </div>
          <div class="card__field">
            <label class="card__label">ALIAS (opcional)</label>
            <input v-model="nuevoAlias" type="text" class="card__input" placeholder="Ej: Mamá" />
          </div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="handleAgregar" :disabled="loading">
              {{ loading ? '...' : 'GUARDAR' }}
            </button>
          </div>
        </div>

        <!-- Tabs -->
        <div class="filter-bar" style="margin-bottom:1rem">
          <button :class="['filter-bar__pill', tabActivo === 'todos'     && 'filter-bar__pill--active']" @click="tabActivo = 'todos'">Todos</button>
          <button :class="['filter-bar__pill', tabActivo === 'favoritos' && 'filter-bar__pill--active']" @click="tabActivo = 'favoritos'">⭐ Favoritos</button>
        </div>

        <div v-if="listaVisible.length === 0" class="trans-list__empty">
          No hay contactos.
        </div>

        <div
          v-for="c in listaVisible"
          :key="c.id"
          class="trans-list__item trans-list__item--static"
          style="display:flex;align-items:center;justify-content:space-between;gap:.75rem"
        >
          <div>
            <strong>{{ c.alias || c.contacto?.nombre }} {{ !c.alias ? c.contacto?.apellidos : '' }}</strong>
            <div class="trans-list__item-sub">{{ c.contacto?.email }}</div>
            <div class="trans-list__item-sub">{{ c.contacto?.numCuenta }}</div>
          </div>
          <div style="display:flex;gap:.5rem;flex-shrink:0">
            <button class="card__btn card__btn--accept" style="padding:.3rem .7rem;font-size:.8rem"
                    @click="handleToggleFav(c)" :title="c.esFavorito ? 'Quitar favorito' : 'Marcar favorito'">
              {{ c.esFavorito ? '⭐' : '☆' }}
            </button>
            <button class="card__btn card__btn--accept" style="padding:.3rem .7rem;font-size:.8rem"
                    @click="enviarA(c)" title="Enviar dinero">
              ↑ Enviar
            </button>
            <button class="card__btn card__btn--cancel" style="padding:.3rem .7rem;font-size:.8rem"
                    @click="handleEliminar(c)" title="Eliminar contacto">
              ✕
            </button>
          </div>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getContactos, getFavoritos, agregarContacto, toggleFavorito, eliminarContacto, buscarPorEmail } from '../../api'

const router       = useRouter()
const loading      = ref(false)
const error        = ref('')
const success      = ref('')
const contactos    = ref([])
const favoritos    = ref([])
const tabActivo    = ref('todos')
const mostrarForm  = ref(false)
const nuevoEmail   = ref('')
const nuevoAlias   = ref('')

const listaVisible = computed(() =>
  tabActivo.value === 'favoritos' ? favoritos.value : contactos.value
)

async function cargar() {
  error.value = ''
  try {
    const [rc, rf] = await Promise.all([getContactos(), getFavoritos()])
    contactos.value = rc.data || []
    favoritos.value = rf.data || []
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al cargar contactos'
  }
}

async function handleAgregar() {
  const email = nuevoEmail.value.trim()
  if (!email) return
  loading.value = true
  error.value   = ''
  success.value = ''
  try {
    // Primero buscar el username por email
    const res  = await buscarPorEmail(email)
    const user = res.data
    await agregarContacto({ contactoUsername: user.username, alias: nuevoAlias.value.trim() || null })
    success.value  = `${user.nombre} añadido como contacto`
    nuevoEmail.value = ''
    nuevoAlias.value = ''
    mostrarForm.value = false
    await cargar()
  } catch (e) {
    error.value = e.response?.status === 404
      ? 'No se encontró ningún usuario con ese email'
      : e.response?.data?.error || 'Error al añadir contacto'
  } finally {
    loading.value = false
  }
}

async function handleToggleFav(c) {
  try {
    await toggleFavorito(c.id)
    await cargar()
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al actualizar favorito'
  }
}

async function handleEliminar(c) {
  if (!confirm(`¿Eliminar a ${c.alias || c.contacto?.nombre} de tus contactos?`)) return
  try {
    await eliminarContacto(c.id)
    success.value = 'Contacto eliminado'
    await cargar()
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al eliminar contacto'
  }
}

function enviarA(c) {
  router.push({ path: '/transacciones/nueva', query: { destino: c.contacto?.numCuenta } })
}

onMounted(cargar)
</script>
