<template>
  <div class="page">
    <NavBar />

    <main class="page__content">
      <article class="static-page">
        <h1 class="static-page__title">Sobre APIzum</h1>

        <section class="static-page__section">
          <h2>¿Quiénes somos?</h2>
          <p>
            APIzum nació en 2026 con una misión clara: hacer que las transferencias de dinero
            entre personas sean tan sencillas como enviar un mensaje. Somos un equipo de
            desarrolladores apasionados por la tecnología financiera, convencidos de que
            gestionar tu dinero no tiene por qué ser complicado.
          </p>
        </section>

        <section class="static-page__section">
          <h2>Nuestra misión</h2>
          <p>
            Democratizar el acceso a servicios financieros digitales. Queremos que cualquier
            persona, independientemente de sus conocimientos tecnológicos, pueda enviar y
            recibir dinero de forma segura, rápida y sin comisiones ocultas.
          </p>
        </section>

        <section class="static-page__section">
          <h2>¿Cómo funciona?</h2>
          <p>
            Registra tu cuenta en segundos, vincula tu número de cuenta y empieza a transferir.
            Usamos cifrado asimétrico con JWT para proteger cada operación. Tus datos nunca se
            comparten con terceros sin tu consentimiento.
          </p>
        </section>

        <section class="static-page__section">
          <h2>Nuestros valores</h2>
          <p>
            Transparencia, seguridad y simplicidad son los pilares de APIzum. Creemos que
            la confianza se construye siendo honestos con nuestros usuarios: sin letra pequeña,
            sin sorpresas. Si algo falla, lo comunicamos de inmediato y lo arreglamos.
          </p>
        </section>

        <section class="static-page__section">
          <h2>El equipo</h2>
          <p>
            Somos un equipo joven, diverso y distribuido. Nos une la pasión por crear
            productos que las personas realmente quieran usar. Trabajamos en remoto,
            con procesos ágiles y una cultura de mejora continua.
          </p>
        </section>
      </article>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'
</script>
