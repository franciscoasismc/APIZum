<template>
  <!-- Bloque: página de inicio -->
  <div class="page">

    <!-- Componente: barra de navegación -->
    <NavBar />

    <!-- Bloque: sección principal de inicio -->
    <main class="inicio">

      <!-- Elemento: collage fotográfico -->
      <div class="inicio__collage">
        <img src="/images/Collage.webp" alt="Personas disfrutando" />
      </div>

      <!-- Elemento: sección inferior con lema y marca -->
      <div class="inicio__inferior">

        <!-- Elemento: tarjeta oscura con lema -->
        <p class="inicio__lema">
          Tú disfruta de lo que te gusta, que del resto, ya nos encargamos nosotros.
        </p>

        <!-- Elemento: nombre y subtítulo de la marca -->
        <div class="inicio__marca">
          <span class="inicio__marca-nombre">APIZum</span>
          <span class="inicio__marca-sub">Transpasos de dinero simples</span>
        </div>

      </div>
    </main>


    <!-- Componente: pie de página con redes sociales -->
    <Footer />

  </div>
</template>

<script setup>
// Componente de navegación compartido
import NavBar from '../components/NavBar.vue'
// Componente de pie de página con redes sociales
import Footer from '../components/Footer.vue'
</script>