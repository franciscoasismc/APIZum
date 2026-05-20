<template>
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="card card--wide">
        <h2 class="card__header">DATOS</h2>
        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <template v-if="!editando">
          <div class="card__info"><span>USERNAME</span>{{ perfil.username }}</div>
          <div class="card__info"><span>NOMBRE</span>{{ perfil.nombre }}</div>
          <div class="card__info"><span>APELLIDOS</span>{{ perfil.apellidos }}</div>
          <div class="card__info"><span>EMAIL</span>{{ perfil.email }}</div>
          <div class="card__info"><span>ROL</span>{{ perfil.rol }}</div>
          <div class="card__info"><span>CUENTA</span>{{ perfil.numCuenta?.numCuenta || '—' }}</div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="editando = true">ACTUALIZAR</button>
          </div>
        </template>

        <template v-else>
          <div class="card__field">
            <label class="card__label">NOMBRE</label>
            <input v-model="form.nombre" type="text" class="card__input" />
          </div>
          <div class="card__field">
            <label class="card__label">APELLIDOS</label>
            <input v-model="form.apellidos" type="text" class="card__input" />
          </div>
          <div class="card__field">
            <label class="card__label">EMAIL</label>
            <input v-model="form.email" type="email" class="card__input" />
          </div>
          <div class="card__field">
            <label class="card__label">NUEVA CONTRASEÑA</label>
            <input v-model="form.password" type="password" class="card__input" />
          </div>
          <div class="card__field">
            <label class="card__label">REPETIR CONTRASEÑA</label>
            <input v-model="form.repetirPassword" type="password" class="card__input" />
          </div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="handleActualizar" :disabled="loading">
              {{ loading ? '...' : 'ACEPTAR' }}
            </button>
            <button class="card__btn card__btn--cancel" @click="cancelar">CANCELAR</button>
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
import { getPerfil, updatePerfil } from '../../api'

const loading  = ref(false)
const error    = ref('')
const success  = ref('')
const editando = ref(false)
const perfil   = ref({})
const form     = ref({ nombre: '', apellidos: '', email: '', password: '', repetirPassword: '' })

onMounted(async () => {
  try {
    const res = await getPerfil()
    perfil.value = res.data
    form.value = { nombre: res.data.nombre, apellidos: res.data.apellidos, email: res.data.email, password: '', repetirPassword: '' }
  } catch {
    error.value = 'Error al cargar el perfil'
  }
})

function cancelar() {
  editando.value = false
  error.value = ''
}

async function handleActualizar() {
  error.value = ''
  success.value = ''
  loading.value = true
  try {
    const res = await updatePerfil({ ...form.value, username: perfil.value.username })
    perfil.value = res.data
    editando.value = false
    success.value = 'Perfil actualizado correctamente'
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al actualizar'
  } finally {
    loading.value = false
  }
}
</script>
