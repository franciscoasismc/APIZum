<template>
  <!-- Bloque: detalle de usuario (admin) -->
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="card card--wide">
        <h2 class="card__header">DATOS DEL USUARIO</h2>
        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <!-- Modo lectura -->
        <template v-if="!editando">
          <div class="card__info"><span>USERNAME</span>{{ perfil.username }}</div>
          <div class="card__info"><span>NOMBRE</span>{{ perfil.nombre }}</div>
          <div class="card__info"><span>APELLIDOS</span>{{ perfil.apellidos }}</div>
          <div class="card__info"><span>EMAIL</span>{{ perfil.email }}</div>
          <div class="card__info"><span>ROL</span>{{ perfil.rol }}</div>
          <div class="card__info">
            <span>CUENTA</span>
            <!-- Elemento: enlace clicable al detalle de cuenta -->
            <span
              v-if="perfil.numCuenta"
              class="link-cell"
              @click="$router.push(`/admin/cuentas/${perfil.numCuenta.numCuenta}`)"
            >{{ perfil.numCuenta.numCuenta }}</span>
            <span v-else>—</span>
          </div>
          <div class="card__actions">
            <button class="card__btn card__btn--accept" @click="editando = true">ACTUALIZAR</button>
            <button class="card__btn card__btn--danger" @click="confirmarEliminar = true">ELIMINAR</button>
            <button class="card__btn card__btn--cancel" @click="$router.push('/admin/usuarios')">VOLVER</button>
          </div>
        </template>

        <!-- Modo edición -->
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
            <label class="card__label">ROL</label>
            <select v-model="form.rol" class="card__input">
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <div class="card__field">
            <label class="card__label">NUEVA CONTRASEÑA (opcional)</label>
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
            <button class="card__btn card__btn--cancel" @click="editando = false">CANCELAR</button>
          </div>
        </template>

        <!-- Bloque: confirmación de eliminación -->
        <div v-if="confirmarEliminar" class="confirm">
          <p class="confirm__text">¿Seguro que quieres eliminar este usuario?</p>
          <div class="confirm__actions">
            <button class="card__btn card__btn--danger card__btn--compact" @click="handleEliminar">
              SÍ, ELIMINAR
            </button>
            <button class="card__btn card__btn--cancel card__btn--compact" @click="confirmarEliminar = false">
              CANCELAR
            </button>
          </div>
        </div>
      </div>
    </div>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getUsuario, updateUsuario, deleteUsuario } from '../../api'

const route  = useRoute()
const router = useRouter()

const loading           = ref(false)
const error             = ref('')
const success           = ref('')
const editando          = ref(false)
const confirmarEliminar = ref(false)
const perfil            = ref({})
const form              = ref({ nombre: '', apellidos: '', email: '', rol: 'USER', password: '', repetirPassword: '' })

onMounted(async () => {
  try {
    const res = await getUsuario(route.params.username)
    perfil.value = res.data
    form.value = {
      nombre: res.data.nombre,
      apellidos: res.data.apellidos,
      email: res.data.email,
      rol: res.data.rol,
      password: '',
      repetirPassword: ''
    }
  } catch {
    error.value = 'Error al cargar el usuario'
  }
})

async function handleActualizar() {
  error.value   = ''
  success.value = ''
  loading.value = true
  try {
    const res = await updateUsuario(route.params.username, form.value)
    perfil.value   = res.data
    editando.value = false
    success.value  = 'Usuario actualizado correctamente'
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al actualizar'
  } finally {
    loading.value = false
  }
}

async function handleEliminar() {
  try {
    await deleteUsuario(route.params.username)
    router.push('/admin/usuarios')
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al eliminar'
    confirmarEliminar.value = false
  }
}
</script>
