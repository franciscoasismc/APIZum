<template>
  <div class="page">
    <NavBar />
    <div class="page__content">

      <!-- Paso 1: datos del usuario -->
      <div v-if="paso === 1" class="card card--wide">
        <h2 class="card__header">DATOS</h2>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div class="card__field">
          <label class="card__label">USERNAME (9 dígitos)</label>
          <input v-model="form.username" type="text" class="card__input" maxlength="9" placeholder="123456789" />
        </div>
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
          <label class="card__label">CONTRASEÑA</label>
          <input v-model="form.password" type="password" class="card__input" />
        </div>
        <div class="card__field">
          <label class="card__label">REPETIR CONTRASEÑA</label>
          <input v-model="form.repetirPassword" type="password" class="card__input" />
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleRegistro" :disabled="loading">
            {{ loading ? '...' : 'SIGUIENTE' }}
          </button>
          <RouterLink to="/" style="flex:1">
            <button class="card__btn card__btn--cancel" style="width:100%">CANCELAR</button>
          </RouterLink>
        </div>
      </div>

      <!-- Paso 2: número de cuenta -->
      <div v-if="paso === 2" class="card">
        <h2 class="card__header">CREAR CUENTA</h2>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <p class="hint">
          Usuario creado. Ahora añade tu número de cuenta bancaria.
        </p>

        <div class="card__field">
          <label class="card__label">NÚMERO DE CUENTA (empieza por ES)</label>
          <input v-model="numCuenta" type="text" class="card__input" placeholder="ES1234567890" />
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleCuenta" :disabled="loading">
            {{ loading ? '...' : 'ACEPTAR' }}
          </button>
        </div>
      </div>

    </div>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'
import { registro, registroCuenta, login } from '../api'
import { authStore } from '../stores/auth'

const router   = useRouter()
const paso     = ref(1)
const loading  = ref(false)
const error    = ref('')
const numCuenta = ref('')
const form     = ref({
  username: '', nombre: '', apellidos: '',
  email: '', password: '', repetirPassword: ''
})

async function handleRegistro() {
  error.value = ''

  // Validación de campos obligatorios antes de llamar al servidor
  if (!form.value.username.trim())         return (error.value = 'El username es obligatorio')
  if (!form.value.nombre.trim())           return (error.value = 'El nombre es obligatorio')
  if (!form.value.email.trim())            return (error.value = 'El email es obligatorio')
  if (!form.value.password)               return (error.value = 'La contraseña es obligatoria')
  if (!form.value.repetirPassword)        return (error.value = 'Debes repetir la contraseña')

  loading.value = true
  try {
    await registro(form.value)
    paso.value = 2
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al registrar usuario'
  } finally {
    loading.value = false
  }
}

async function handleCuenta() {
  error.value = ''
  loading.value = true
  try {
    await registroCuenta({ username: form.value.username, numCuenta: numCuenta.value })
    const res = await login({ username: form.value.username, password: form.value.password })
    authStore.login(res.data.token, res.data.username)
    router.push('/perfil')
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al crear la cuenta'
  } finally {
    loading.value = false
  }
}
</script>
