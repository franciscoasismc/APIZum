<template>
  <!-- Bloque: detalle de cuenta (admin) -->
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="card">
        <h1 class="card__header">DATOS DE LA CUENTA</h1>
        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <div class="card__info"><span>NÚMERO DE CUENTA</span>{{ cuenta.numCuenta }}</div>

        <!-- Saldo: vista o edición -->
        <template v-if="!editandoSaldo">
          <div class="card__info">
            <span>SALDO</span>
            {{ cuenta.saldo }} €
            <button class="card__btn card__btn--accept" style="margin-left:auto;width:auto;padding:0.2rem 0.8rem" @click="startEditarSaldo">EDITAR</button>
          </div>
        </template>
        <template v-else>
          <div class="card__field">
            <label class="card__label">NUEVO SALDO (€)</label>
            <input
              v-model.number="nuevoSaldo"
              type="number"
              min="0"
              step="0.01"
              class="card__input"
            />
          </div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="handleGuardarSaldo" :disabled="loadingSaldo">
              {{ loadingSaldo ? '...' : 'GUARDAR' }}
            </button>
            <button class="card__btn card__btn--cancel" @click="editandoSaldo = false">CANCELAR</button>
          </div>
        </template>

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
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getCuentaAdmin, updateCuentaAdmin } from '../../api'

const route        = useRoute()
const error        = ref('')
const success      = ref('')
const cuenta       = ref({})
const editandoSaldo = ref(false)
const nuevoSaldo   = ref(0)
const loadingSaldo = ref(false)

onMounted(async () => {
  try {
    const res = await getCuentaAdmin(route.params.numCuenta)
    cuenta.value = res.data
  } catch {
    error.value = 'Error al cargar la cuenta'
  }
})

function startEditarSaldo() {
  nuevoSaldo.value = cuenta.value.saldo
  editandoSaldo.value = true
}

async function handleGuardarSaldo() {
  if (nuevoSaldo.value < 0) { error.value = 'El saldo no puede ser negativo'; return }
  error.value   = ''
  success.value = ''
  loadingSaldo.value = true
  try {
    const res = await updateCuentaAdmin(cuenta.value.numCuenta, nuevoSaldo.value)
    cuenta.value = res.data
    editandoSaldo.value = false
    success.value = 'Saldo actualizado correctamente'
  } catch (e) {
    error.value = e.response?.data?.message || e.response?.data?.error || 'Error al actualizar el saldo'
  } finally {
    loadingSaldo.value = false
  }
}
</script>
