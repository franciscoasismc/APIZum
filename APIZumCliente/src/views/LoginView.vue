<template>
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="card">
        <h2 class="card__header">DATOS</h2>

        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div class="card__field">
          <label class="card__label">USERNAME</label>
          <input v-model="form.username" type="text" class="card__input" placeholder="123456789" />
        </div>
        <div class="card__field">
          <label class="card__label">CONTRASEÑA</label>
          <input v-model="form.password" type="password" class="card__input" placeholder="••••••••" />
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleLogin" :disabled="loading">
            {{ loading ? '...' : 'ACEPTAR' }}
          </button>
          <RouterLink to="/" style="flex:1">
            <button class="card__btn card__btn--cancel" style="width:100%">CANCELAR</button>
          </RouterLink>
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
import { login } from '../api'
import { authStore } from '../stores/auth'

const router = useRouter()
const loading = ref(false)
const error   = ref('')
const form    = ref({ username: '', password: '' })

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    const res = await login(form.value)
    authStore.login(res.data.token, res.data.username)
    router.push(authStore.isAdmin ? '/admin/usuarios' : '/perfil')
  } catch (e) {
    error.value = e.response?.data?.error || 'Credenciales incorrectas'
  } finally {
    loading.value = false
  }
}
</script>
