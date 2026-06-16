<template>
  <div class="page">
    <NavBar />
    <main class="page__content">
      <div class="card">
        <h1 class="card__header">NUEVA TRANSACCIÓN</h1>
        <div v-if="error"   class="alert alert--error">{{ error }}</div>
        <div v-if="success" class="alert alert--success">{{ success }}</div>

        <!-- Búsqueda por email -->
        <div class="card__field">
          <label for="trans-email" class="card__label">BUSCAR POR EMAIL (opcional)</label>
          <div style="display:flex;gap:.5rem">
            <input
              id="trans-email"
              v-model="emailBusqueda"
              type="email"
              class="card__input"
              placeholder="destinatario@ejemplo.com"
              @keyup.enter="buscarEmail"
            />
            <button class="card__btn card__btn--accept" style="flex-shrink:0" @click="buscarEmail" :disabled="buscando">
              {{ buscando ? '...' : 'Buscar' }}
            </button>
          </div>
          <span v-if="errorBusqueda" class="field-error" role="alert">{{ errorBusqueda }}</span>
          <span v-if="nombreEncontrado" class="field-ok">✓ {{ nombreEncontrado }}</span>
        </div>

        <div class="card__field">
          <label for="trans-destino" class="card__label">CUENTA DESTINO (IBAN) <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="trans-destino"
            v-model="form.numCuentaDestino"
            type="text"
            class="card__input"
            placeholder="ES9121000418450200051332"
            @blur="tocar('numCuentaDestino')"
            :class="{ 'input--error': tocado.numCuentaDestino && !cuentaDestinoValida }"
          />
          <span v-if="tocado.numCuentaDestino && !form.numCuentaDestino.trim()" class="field-error" role="alert">
            La cuenta destino es obligatoria.
          </span>
          <span v-else-if="tocado.numCuentaDestino && form.numCuentaDestino.trim() && !cuentaDestinoValida" class="field-error" role="alert">
            Introduce un IBAN español válido (ES + 22 dígitos, mod 97 = 1).
          </span>
          <span v-else-if="form.numCuentaDestino.trim() && cuentaDestinoValida" class="field-ok">✓ IBAN válido</span>
        </div>

        <div class="card__field">
          <label for="trans-cantidad" class="card__label">CANTIDAD (€) <span aria-hidden="true" style="color:var(--color-danger)">*</span></label>
          <input
            id="trans-cantidad"
            v-model.number="form.cantidad"
            type="number"
            min="0.01"
            step="0.01"
            class="card__input"
            @blur="tocar('cantidad')"
            :class="{ 'input--error': tocado.cantidad && !(form.cantidad > 0) }"
          />
          <span v-if="tocado.cantidad && !(form.cantidad > 0)" class="field-error" role="alert">
            Introduce una cantidad mayor que 0.
          </span>
        </div>

        <div class="card__field">
          <label for="trans-descripcion" class="card__label">DESCRIPCIÓN</label>
          <input
            id="trans-descripcion"
            v-model="form.descripcion"
            type="text"
            class="card__input"
            placeholder="Opcional"
          />
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--accept" @click="handleEnviar" :disabled="loading">
            {{ loading ? '...' : 'ENVIAR' }}
          </button>
          <button class="card__btn card__btn--cancel" @click="$router.push('/transacciones')">CANCELAR</button>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { crearTransaccion, buscarPorEmail } from '../../api'

const router        = useRouter()
const route         = useRoute()
const loading       = ref(false)
const error         = ref('')
const success       = ref('')
const emailBusqueda = ref('')
const buscando      = ref(false)
const errorBusqueda = ref('')
const nombreEncontrado = ref('')

const form   = reactive({ numCuentaDestino: '', cantidad: null, descripcion: '' })
const tocado = reactive({ numCuentaDestino: false, cantidad: false })

// ISO 13616 mod-97 IBAN validation
function validarIBAN(iban) {
  const s = iban.replace(/\s/g, '').toUpperCase()
  if (!/^ES\d{22}$/.test(s)) return false
  const rearranged = s.slice(4) + s.slice(0, 4)
  const numeric = rearranged.split('').map(c => isNaN(c) ? (c.charCodeAt(0) - 55).toString() : c).join('')
  let rem = 0
  for (const chunk of numeric.match(/.{1,9}/g)) {
    rem = parseInt(rem.toString() + chunk, 10) % 97
  }
  return rem === 1
}

const cuentaDestinoValida = computed(() => validarIBAN(form.numCuentaDestino))

function tocar(campo) { tocado[campo] = true }

// Pre-fill from ContactosView (?destino=ESXX...)
onMounted(() => {
  if (route.query.destino) form.numCuentaDestino = route.query.destino
})

async function buscarEmail() {
  const email = emailBusqueda.value.trim()
  if (!email) return
  buscando.value     = true
  errorBusqueda.value = ''
  nombreEncontrado.value = ''
  try {
    const res = await buscarPorEmail(email)
    const u   = res.data
    nombreEncontrado.value  = `${u.nombre} ${u.apellidos}`
    form.numCuentaDestino   = u.numCuenta || ''
  } catch (e) {
    errorBusqueda.value = e.response?.status === 404
      ? 'No se encontró ningún usuario con ese email'
      : 'Error al buscar usuario'
  } finally {
    buscando.value = false
  }
}

async function handleEnviar() {
  Object.keys(tocado).forEach(k => tocado[k] = true)
  if (!cuentaDestinoValida.value || !(form.cantidad > 0)) return

  error.value   = ''
  success.value = ''
  loading.value = true
  try {
    await crearTransaccion(form)
    success.value = 'Transferencia realizada correctamente'
    setTimeout(() => router.push('/transacciones'), 1500)
  } catch (e) {
    error.value = e.response?.data?.error || 'Error al realizar la transferencia'
  } finally {
    loading.value = false
  }
}
</script>
