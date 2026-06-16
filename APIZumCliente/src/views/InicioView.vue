<template>
  <div class="page">
    <NavBar />

    <main class="inicio">
      <!-- Hero Slider -->
      <section class="slider" aria-label="Presentación APIzum" aria-roledescription="carrusel">
        <div
          class="slider__track"
          :style="{ transform: `translateX(-${current * 100}%)` }"
        >
          <article
            v-for="(slide, i) in slides"
            :key="i"
            class="slider__slide"
            :aria-hidden="i !== current"
            role="group"
            :aria-roledescription="'Diapositiva ' + (i + 1) + ' de ' + slides.length"
          >
            <div class="slider__img-wrap">
              <img :src="slide.img" :alt="slide.label" />
            </div>
            <div class="slider__text">
              <h1 class="slider__label">{{ slide.label }}</h1>
              <p class="slider__sublabel">{{ slide.sub }}</p>
            </div>
          </article>
        </div>

        <!-- Flecha anterior -->
        <button
          class="slider__arrow slider__arrow--prev"
          @click="prev"
          aria-label="Anterior"
        >&#8592;</button>

        <!-- Flecha siguiente -->
        <button
          class="slider__arrow slider__arrow--next"
          @click="next"
          aria-label="Siguiente"
        >&#8594;</button>

        <!-- Dots de navegación -->
        <div class="slider__dots" role="tablist" aria-label="Diapositivas">
          <button
            v-for="(slide, i) in slides"
            :key="'dot-' + i"
            :class="['slider__dot', { 'slider__dot--active': i === current }]"
            @click="goTo(i)"
            role="tab"
            :aria-selected="i === current"
            :aria-label="'Ir a ' + slide.label"
          ></button>
        </div>
      </section>

      <!-- Sección features: filas alternadas texto / imagen -->
      <section class="features" aria-label="Características">
        <!-- Fila 1: texto izquierda, imagen derecha -->
        <article class="feature-row">
          <div class="feature-row__text">
            <h2 class="feature-row__title">Envíos de dinero <span aria-hidden="true">→</span></h2>
          </div>
          <div class="feature-row__img">
            <img src="/images/Envío.webp" alt="Envío de dinero" />
          </div>
        </article>

        <!-- Fila 2: imagen izquierda, texto derecha -->
        <article class="feature-row feature-row--reverse">
          <div class="feature-row__img">
            <img src="/images/Recibo.webp" alt="Recibo de dinero" />
          </div>
          <div class="feature-row__text">
            <h2 class="feature-row__title"><span aria-hidden="true">←</span> Recibir dinero</h2>
          </div>
        </article>
      </section>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'

const slides = [
  {
    img: '/images/Descubre.webp',
    label: 'Descubre',
    sub: 'Encuentra nuevas experiencias y paga al momento con APIzum.'
  },
  {
    img: '/images/Viaja.webp',
    label: 'Viaja',
    sub: 'Viaja sin límites y gestiona tu dinero desde cualquier lugar.'
  },
  {
    img: '/images/Comparte.webp',
    label: 'Comparte',
    sub: 'Divide gastos con amigos y familiares en segundos.'
  },
  {
    img: '/images/Entrena.webp',
    label: 'Entrena',
    sub: 'Págale a tu entrenador, tu gimnasio o tu club de manera sencilla.'
  }
]

const current = ref(0)
let timer = null

function next()        { current.value = (current.value + 1) % slides.length; restart() }
function prev()        { current.value = (current.value - 1 + slides.length) % slides.length; restart() }
function goTo(i)       { current.value = i; restart() }
function startTimer()  { timer = setInterval(next, 3000) }
function restart()     { clearInterval(timer); startTimer() }

onMounted(startTimer)
onUnmounted(() => clearInterval(timer))
</script>
