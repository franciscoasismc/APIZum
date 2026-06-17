<template>
  <div class="page">
    <NavBar />
    <main class="page__content">

      <!-- Paso 1: datos del usuario -->
      <section v-if="paso === 1" class="card card--wide" aria-label="Registro de usuario - Paso 1">
        <h1 class="card__header">DATOS</h1>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div class="card__field">
          <label for="reg-username" class="card__label">USERNAME (9 dígitos) <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="reg-username"
            v-model="form.username"
            type="text"
            class="card__input"
            maxlength="9"
            placeholder="123456789"
            @blur="tocar('username')"
            :class="{ 'input--error': tocado.username && !usernameValido }"
          />
          <span v-if="tocado.username && !form.username.trim()" class="field-error" role="alert">
            El username es obligatorio.
          </span>
          <span v-else-if="tocado.username && form.username.trim() && !usernameValido" class="field-error" role="alert">
            El username debe tener exactamente 9 dígitos numéricos.
          </span>
        </div>

        <div class="card__field">
          <label for="reg-nombre" class="card__label">NOMBRE <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="reg-nombre"
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
          <label for="reg-apellidos" class="card__label">APELLIDOS</label>
          <input
            id="reg-apellidos"
            v-model="form.apellidos"
            type="text"
            class="card__input"
          />
        </div>

        <div class="card__field">
          <label for="reg-email" class="card__label">EMAIL <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="reg-email"
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
          <label for="reg-password" class="card__label">CONTRASEÑA <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="reg-password"
            v-model="form.password"
            type="password"
            class="card__input"
            @blur="tocar('password')"
            :class="{ 'input--error': tocado.password && !form.password }"
          />
          <span v-if="tocado.password && !form.password" class="field-error" role="alert">
            La contraseña es obligatoria.
          </span>
        </div>

        <div class="card__field">
          <label for="reg-repetir" class="card__label">REPETIR CONTRASEÑA <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="reg-repetir"
            v-model="form.repetirPassword"
            type="password"
            class="card__input"
            @blur="tocar('repetirPassword')"
            :class="{ 'input--error': tocado.repetirPassword && (!form.repetirPassword || form.password !== form.repetirPassword) }"
          />
          <span v-if="tocado.repetirPassword && !form.repetirPassword" class="field-error" role="alert">
            Debes repetir la contraseña.
          </span>
          <span v-else-if="tocado.repetirPassword && form.repetirPassword && form.password !== form.repetirPassword" class="field-error" role="alert">
            Las contraseñas no coinciden.
          </span>
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleRegistro" :disabled="loading">
            {{ loading ? '...' : 'SIGUIENTE' }}
          </button>
          <RouterLink to="/" style="flex:1">
            <button class="card__btn card__btn--cancel" style="width:100%">CANCELAR</button>
          </RouterLink>
        </div>
      </section>

      <!-- Paso 2: número de cuenta -->
      <section v-if="paso === 2" class="card" aria-label="Registro de usuario - Paso 2">
        <h1 class="card__header">CREAR CUENTA</h1>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <p class="hint">
          Usuario creado. Ahora añade tu número de cuenta bancaria.
        </p>

        <div class="card__field">
          <label for="reg-numcuenta" class="card__label">NÚMERO DE CUENTA (empieza por ES) <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="reg-numcuenta"
            v-model="numCuenta"
            type="text"
            class="card__input"
            placeholder="ES1234567890"
            @blur="tocadoCuenta = true"
            :class="{ 'input--error': tocadoCuenta && !numCuentaValido }"
          />
          <span v-if="tocadoCuenta && !numCuenta.trim()" class="field-error" role="alert">
            El número de cuenta es obligatorio.
          </span>
          <span v-else-if="tocadoCuenta && numCuenta.trim() && !numCuentaValido" class="field-error" role="alert">
            El número de cuenta debe empezar por ES.
          </span>
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleCuenta" :disabled="loading">
            {{ loading ? '...' : 'ACEPTAR' }}
          </button>
        </div>
      </section>

    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'
import { registro, registroCuenta, login } from '../api'
import { authStore } from '../stores/auth'

const router     = useRouter()
const paso       = ref(1)
const loading    = ref(false)
const error      = ref('')
const numCuenta  = ref('')
const tocadoCuenta = ref(false)
let   tempToken  = '' // JWT temporal emitido tras el registro (paso 1 → paso 2)

const form = reactive({
  username: '', nombre: '', apellidos: '',
  email: '', password: '', repetirPassword: ''
})
const tocado = reactive({
  username: false, nombre: false, apellidos: false,
  email: false, password: false, repetirPassword: false
})

const emailValido    = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
const usernameValido = computed(() => /^\d{9}$/.test(form.username))
// Validación IBAN completa (ISO 13616 — módulo 97)
function validarIBAN(iban) {
  const s = iban.replace(/\s/g, '').toUpperCase()
  if (!/^[A-Z]{2}\d{2}[A-Z0-9]{1,30}$/.test(s)) return false
  const rearranged = s.slice(4) + s.slice(0, 4)
  const numeric = rearranged.split('').map(c => isNaN(c) ? (c.charCodeAt(0) - 55).toString() : c).join('')
  let remainder = 0
  for (const chunk of numeric.match(/.{1,9}/g)) {
    remainder = parseInt(remainder.toString() + chunk, 10) % 97
  }
  return remainder === 1
}
const numCuentaValido = computed(() => validarIBAN(numCuenta.value))

function tocar(campo) { tocado[campo] = true }

function paso1Valido() {
  return (
    usernameValido.value &&
    form.nombre.trim() &&
    emailValido.value &&
    form.password &&
    form.repetirPassword &&
    form.password === form.repetirPassword
  )
}

async function handleRegistro() {
  Object.keys(tocado).forEach(k => tocado[k] = true)
  if (!paso1Valido()) return

  error.value = ''
  loading.value = true
  try {
    const res = await registro(form)
    // El backend devuelve un JWT temporal para autenticar el paso 2
    tempToken = res.data.token
    localStorage.setItem('token', tempToken)
    paso.value = 2
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al registrar usuario'
  } finally {
    loading.value = false
  }
}

async function handleCuenta() {
  tocadoCuenta.value = true
  if (!numCuentaValido.value) return

  error.value = ''
  loading.value = true
  try {
    // El token temporal del paso 1 ya está en localStorage; el interceptor lo añade
    await registroCuenta({ numCuenta: numCuenta.value })
    // Autenticar con credenciales reales para obtener el JWT definitivo
    const res = await login({ username: form.username, password: form.password })
    authStore.login(res.data.token, res.data.username)
    router.push('/perfil')
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al crear la cuenta'
  } finally {
    loading.value = false
  }
}
</script>
