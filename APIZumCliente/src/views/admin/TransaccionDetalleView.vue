<template>
  <!-- Bloque: detalle de transacción (admin) -->
  <div class="page">
    <NavBar />
    <div class="page__content">
      <div class="card card--wide">
        <h2 class="card__header">DATOS DE LA TRANSACCIÓN</h2>
        <div v-if="error" class="alert alert--error">{{ error }}</div>

        <div class="card__info"><span>ID</span>{{ trans.idTransaccion }}</div>
        <div class="card__info"><span>CANTIDAD</span>{{ trans.cantidad }} €</div>
        <div class="card__info"><span>DESCRIPCIÓN</span>{{ trans.descripcion || '—' }}</div>
        <div class="card__info"><span>FECHA</span>{{ formatFecha(trans.fecha) }}</div>

        <!-- Elemento: enlace a detalle de cuenta origen -->
        <div class="card__info">
          <span>CUENTA ORIGEN</span>
          <span
            class="link-cell"
            @click="$router.push(`/admin/cuentas/${trans.cuentaOrigen?.numCuenta}`)"
          >{{ trans.cuentaOrigen?.numCuenta }}</span>
        </div>

        <!-- Elemento: enlace a detalle de cuenta destino -->
        <div class="card__info">
          <span>CUENTA DESTINO</span>
          <span
            class="link-cell"
            @click="$router.push(`/admin/cuentas/${trans.cuentaDestino?.numCuenta}`)"
          >{{ trans.cuentaDestino?.numCuenta }}</span>
        </div>

        <div class="card__actions">
          <button class="card__btn card__btn--cancel" @click="$router.push('/admin/transacciones')">
            VOLVER
          </button>
        </div>
      </div>
    </div>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import Footer from '../../components/Footer.vue'
import { getTransaccion } from '../../api'

const route = useRoute()
const error = ref('')
const trans = ref({})

onMounted(async () => {
  try {
    const res = await getTransaccion(route.params.id)
    trans.value = res.data
  } catch {
    error.value = 'Error al cargar la transacción'
  }
})

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleString('es-ES')
}
</script>
