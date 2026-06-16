<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="card card--wide">
        <h1 class="card__header">DATOS</h1>
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
            <button class="card__btn card__btn--accept" @click="startEditar">ACTUALIZAR</button>
          </div>
        </template>

        <template v-else>
          <div class="card__field">
            <label for="perf-nombre" class="card__label">NOMBRE <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
            <input
              id="perf-nombre"
              v-model="form.nombre"
              type="text"
              class="card__input"
              @blur="tocar('nombre')"
              :class="{ 'input--error': tocado.nombre && !form.nombre.trim() }"
            />
            <span v-if="tocado.nombre && !form.nombre.trim()" class="field-error" role="alert">
              El nombre es obligatorio.
            </span>
          </div>

          <div class="card__field">
            <label for="perf-apellidos" class="card__label">APELLIDOS <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
            <input
              id="perf-apellidos"
              v-model="form.apellidos"
              type="text"
              class="card__input"
              @blur="tocar('apellidos')"
              :class="{ 'input--error': tocado.apellidos && !form.apellidos.trim() }"
            />
            <span v-if="tocado.apellidos && !form.apellidos.trim()" class="field-error" role="alert">
              Los apellidos son obligatorios.
            </span>
          </div>

          <div class="card__field">
            <label for="perf-email" class="card__label">EMAIL <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
            <input
              id="perf-email"
              v-model="form.email"
              type="email"
              class="card__input"
              @blur="tocar('email')"
              :class="{ 'input--error': tocado.email && (!form.email.trim() || !emailValido) }"
            />
            <span v-if="tocado.email && !form.email.trim()" class="field-error" role="alert">
              El email es obligatorio.
            </span>
            <span v-else-if="tocado.email && form.email.trim() && !emailValido" class="field-error" role="alert">
              Introduce un email válido.
            </span>
          </div>

          <div class="card__field">
            <label for="perf-password" class="card__label">NUEVA CONTRASEÑA</label>
            <input
              id="perf-password"
              v-model="form.password"
              type="password"
              class="card__input"
              @blur="tocar('password')"
            />
          </div>

          <div class="card__field">
            <label for="perf-repetir" class="card__label">REPETIR CONTRASEÑA</label>
            <input
              id="perf-repetir"
              v-model="form.repetirPassword"
              type="password"
              class="card__input"
              @blur="tocar('repetirPassword')"
              :class="{ 'input--error': tocado.repetirPassword && form.password !== form.repetirPassword }"
            />
            <span v-if="tocado.repetirPassword && form.password !== form.repetirPassword" class="field-error" role="alert">
              Las contraseñas no coinciden.
            </span>
          </div>

          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="handleActualizar" :disabled="loading">
              {{ loading ? '...' : 'ACEPTAR' }}
            </button>
            <button class="card__btn card__btn--cancel" @click="cancelar">CANCELAR</button>
          </div>
        </template>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getPerfil, updatePerfil } from '../../api'

const loading  = ref(false)
const error    = ref('')
const success  = ref('')
const editando = ref(false)
const perfil   = ref({})
const form     = reactive({ nombre: '', apellidos: '', email: '', password: '', repetirPassword: '' })
const tocado   = reactive({ nombre: false, apellidos: false, email: false, password: false, repetirPassword: false })

const emailValido = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))

function tocar(campo) { tocado[campo] = true }

onMounted(async () => {
  try {
    const res = await getPerfil()
    perfil.value = res.data
    form.nombre   = res.data.nombre
    form.apellidos = res.data.apellidos
    form.email    = res.data.email
    form.password = ''
    form.repetirPassword = ''
  } catch {
    error.value = 'Error al cargar el perfil'
  }
})

function startEditar() {
  Object.keys(tocado).forEach(k => tocado[k] = false)
  editando.value = true
}

function cancelar() {
  editando.value = false
  error.value = ''
}

async function handleActualizar() {
  Object.keys(tocado).forEach(k => tocado[k] = true)

  const valido =
    form.nombre.trim() &&
    form.apellidos.trim() &&
    emailValido.value &&
    form.password === form.repetirPassword

  if (!valido) return

  error.value  = ''
  success.value = ''
  loading.value = true
  try {
    const res = await updatePerfil({ ...form, username: perfil.value.username })
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
