<template>
  <div class="page">
    <NavBar />
    <main class="page__content">

      <!-- ── Datos del perfil ───────────────────────────────── -->
      <div class="card card--wide">
        <h1 class="card__header">DATOS</h1>
        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <template v-if="!editando">
          <div class="card__info"><span>USERNAME</span>{{ perfil.username }}</div>
          <div class="card__info"><span>NOMBRE</span>{{ perfil.nombre }}</div>
          <div class="card__info"><span>APELLIDOS</span>{{ perfil.apellidos || '—' }}</div>
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
            <label for="perf-apellidos" class="card__label">APELLIDOS</label>
            <input
              id="perf-apellidos"
              v-model="form.apellidos"
              type="text"
              class="card__input"
            />
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
            <label for="perf-password" class="card__label">NUEVA CONTRASEÑA (dejar vacío para no cambiar)</label>
            <input
              id="perf-password"
              v-model="form.password"
              type="password"
              class="card__input"
              @blur="tocar('password')"
              :class="{ 'input--error': tocado.password && form.password && !passwordValida }"
            />
            <span v-if="tocado.password && form.password && !passwordValida" class="field-error" role="alert">
              Debe tener entre 8 y 20 caracteres alfanuméricos.
            </span>
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

      <!-- ── Añadir cuenta (solo si no tiene) ──────────────── -->
      <div v-if="!perfil.numCuenta && !editando" class="card card--wide" style="margin-top:1rem">
        <h2 class="card__header">AÑADIR CUENTA</h2>
        <div v-if="errorCuenta" class="alert alert--error">{{ errorCuenta }}</div>
        <div v-if="successCuenta" class="alert alert--success">{{ successCuenta }}</div>

        <p class="hint">Aún no tienes cuenta bancaria asociada. Añade tu IBAN español.</p>

        <div class="card__field">
          <label for="perf-numcuenta" class="card__label">NÚMERO DE CUENTA (IBAN) <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="perf-numcuenta"
            v-model="numCuenta"
            type="text"
            class="card__input"
            placeholder="ES1234567890123456789012"
            @blur="tocadoCuenta = true"
            :class="{ 'input--error': tocadoCuenta && !numCuentaValido }"
          />
          <span v-if="tocadoCuenta && !numCuenta.trim()" class="field-error" role="alert">
            El número de cuenta es obligatorio.
          </span>
          <span v-else-if="tocadoCuenta && numCuenta.trim() && !numCuentaValido" class="field-error" role="alert">
            Debe ser un IBAN español válido (ISO 13616).
          </span>
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleAnadirCuenta" :disabled="loadingCuenta">
            {{ loadingCuenta ? '...' : 'AÑADIR CUENTA' }}
          </button>
        </div>
      </div>

    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getPerfil, updatePerfil, registroCuenta } from '../../api'

const loading  = ref(false)
const error    = ref('')
const success  = ref('')
const editando = ref(false)
const perfil   = ref({})
const form     = reactive({ nombre: '', apellidos: '', email: '', password: '', repetirPassword: '' })
const tocado   = reactive({ nombre: false, email: false, password: false, repetirPassword: false })

// Cuenta
const numCuenta      = ref('')
const tocadoCuenta   = ref(false)
const loadingCuenta  = ref(false)
const errorCuenta    = ref('')
const successCuenta  = ref('')

const emailValido  = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
const passwordValida = computed(() => /^[a-zA-Z0-9]{8,20}$/.test(form.password))

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

onMounted(async () => {
  try {
    const res = await getPerfil()
    perfil.value = res.data
    form.nombre    = res.data.nombre || ''
    form.apellidos = res.data.apellidos || ''
    form.email     = res.data.email || ''
    form.password  = ''
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

  // Contraseña: solo validar formato si se ha rellenado
  const passwordOk = !form.password || (passwordValida.value && form.password === form.repetirPassword)

  const valido = form.nombre.trim() && emailValido.value && passwordOk

  if (!valido) return

  error.value   = ''
  success.value = ''
  loading.value = true
  try {
    const payload = { nombre: form.nombre, apellidos: form.apellidos, email: form.email }
    if (form.password) {
      payload.password = form.password
      payload.repetirPassword = form.repetirPassword
    }
    const res = await updatePerfil(payload)
    perfil.value = res.data
    editando.value = false
    success.value = 'Perfil actualizado correctamente'
  } catch (e) {
    error.value = e.response?.data?.message || e.response?.data?.error || 'Error al actualizar'
  } finally {
    loading.value = false
  }
}

async function handleAnadirCuenta() {
  tocadoCuenta.value = true
  if (!numCuentaValido.value) return

  errorCuenta.value   = ''
  successCuenta.value = ''
  loadingCuenta.value = true
  try {
    await registroCuenta({ numCuenta: numCuenta.value })
    const res = await getPerfil()
    perfil.value = res.data
    successCuenta.value = 'Cuenta añadida correctamente'
  } catch (e) {
    errorCuenta.value = e.response?.data?.message || e.response?.data?.error || 'Error al añadir la cuenta'
  } finally {
    loadingCuenta.value = false
  }
}
</script>
