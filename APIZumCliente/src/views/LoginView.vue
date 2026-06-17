<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <section class="card" aria-label="Iniciar sesión">
        <h1 class="card__header">INICIAR SESIÓN</h1>

        <div v-if="error" class="alert alert--error" role="alert">{{ error }}</div>

        <div class="card__field">
          <label for="login-username" class="card__label">USERNAME (9 dígitos) <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="login-username"
            v-model="form.username"
            type="text"
            class="card__input"
            placeholder="123456789"
            maxlength="9"
            autocomplete="username"
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
          <label for="login-password" class="card__label">CONTRASEÑA <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="login-password"
            v-model="form.password"
            type="password"
            class="card__input"
            placeholder="••••••••"
            autocomplete="current-password"
            @blur="tocar('password')"
            :class="{ 'input--error': tocado.password && !form.password }"
          />
          <span v-if="tocado.password && !form.password" class="field-error" role="alert">
            La contraseña es obligatoria.
          </span>
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleLogin" :disabled="loading">
            {{ loading ? '...' : 'ACEPTAR' }}
          </button>
          <RouterLink to="/" style="flex:1">
            <button class="card__btn card__btn--cancel" style="width:100%">CANCELAR</button>
          </RouterLink>
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
import { login } from '../api'
import { authStore } from '../stores/auth'

const router  = useRouter()
const loading = ref(false)
const error   = ref('')
const form    = reactive({ username: '', password: '' })
const tocado  = reactive({ username: false, password: false })

const usernameValido = computed(() => /^\d{9}$/.test(form.username))

function tocar(campo) { tocado[campo] = true }

async function handleLogin() {
  Object.keys(tocado).forEach(k => tocado[k] = true)
  if (!usernameValido.value || !form.password) return

  error.value = ''
  loading.value = true
  try {
    const res = await login(form)
    authStore.login(res.data.token, res.data.username)
    router.push(authStore.isAdmin ? '/admin/usuarios' : '/perfil')
  } catch (e) {
    error.value = e.response?.data?.error || 'Credenciales incorrectas'
  } finally {
    loading.value = false
  }
}
</script>
