<template>
  <div class="page">
    <NavBar />

    <main class="page__content">
      <article class="static-page">
        <h1 class="static-page__title">Contacto</h1>

        <section class="static-page__section">
          <h2>¿En qué podemos ayudarte?</h2>
          <p>Rellena el formulario y te responderemos en un plazo máximo de 48 horas laborables.</p>
        </section>

        <form class="contact-form" @submit.prevent="enviar" novalidate aria-label="Formulario de contacto">

          <!-- Nombre -->
          <div>
            <label for="nombre">
              Nombre <span aria-hidden="true" style="color:var(--color-danger)">*</span>
            </label>
            <input
              id="nombre"
              v-model="form.nombre"
              type="text"
              placeholder="Tu nombre"
              autocomplete="given-name"
              @blur="tocar('nombre')"
              :class="{ 'input--error': tocado.nombre && !form.nombre.trim() }"
            />
            <span v-if="tocado.nombre && !form.nombre.trim()" class="field-error" role="alert">
              El nombre es obligatorio.
            </span>
          </div>

          <!-- Email -->
          <div>
            <label for="email">
              Correo electrónico <span aria-hidden="true" style="color:var(--color-danger)">*</span>
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              placeholder="tu@email.com"
              autocomplete="email"
              @blur="tocar('email')"
              :class="{ 'input--error': tocado.email && !emailValido }"
            />
            <span v-if="tocado.email && !form.email.trim()" class="field-error" role="alert">
              El correo electrónico es obligatorio.
            </span>
            <span v-else-if="tocado.email && form.email.trim() && !emailValido" class="field-error" role="alert">
              Introduce un correo electrónico válido.
            </span>
          </div>

          <!-- Asunto -->
          <div>
            <label for="asunto">
              Asunto <span aria-hidden="true" style="color:var(--color-danger)">*</span>
            </label>
            <input
              id="asunto"
              v-model="form.asunto"
              type="text"
              placeholder="¿Sobre qué quieres contactarnos?"
              @blur="tocar('asunto')"
              :class="{ 'input--error': tocado.asunto && !form.asunto.trim() }"
            />
            <span v-if="tocado.asunto && !form.asunto.trim()" class="field-error" role="alert">
              El asunto es obligatorio.
            </span>
          </div>

          <!-- Mensaje -->
          <div>
            <label for="mensaje">
              Mensaje <span aria-hidden="true" style="color:var(--color-danger)">*</span>
            </label>
            <textarea
              id="mensaje"
              v-model="form.mensaje"
              placeholder="Escribe tu mensaje aquí..."
              @blur="tocar('mensaje')"
              :class="{ 'input--error': tocado.mensaje && !form.mensaje.trim() }"
            ></textarea>
            <span v-if="tocado.mensaje && !form.mensaje.trim()" class="field-error" role="alert">
              El mensaje es obligatorio.
            </span>
          </div>

          <div v-if="enviado" class="alert alert--success" role="status" aria-live="polite">
            ¡Mensaje enviado! Te responderemos en breve.
          </div>

          <button type="submit" class="contact-form__btn">ENVIAR MENSAJE</button>
        </form>
      </article>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'

const form    = reactive({ nombre: '', email: '', asunto: '', mensaje: '' })
const tocado  = reactive({ nombre: false, email: false, asunto: false, mensaje: false })
const enviado = ref(false)

const emailValido = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))

function tocar(campo) {
  tocado[campo] = true
}

function enviar() {
  // Marcar todos como tocados para mostrar todos los errores
  Object.keys(tocado).forEach(k => tocado[k] = true)

  if (!form.nombre.trim() || !emailValido.value || !form.asunto.trim() || !form.mensaje.trim()) return

  enviado.value = true
  Object.keys(form).forEach(k => form[k] = '')
  Object.keys(tocado).forEach(k => tocado[k] = false)
}
</script>
