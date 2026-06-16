<template>
  <div class="page">
    <NavBar />

    <main class="page__content">
      <article class="static-page">
        <h1 class="static-page__title">Aviso Legal</h1>

        <section class="static-page__section">
          <h2>1. Datos identificativos</h2>
          <p>
            En cumplimiento del artículo 10 de la Ley 34/2002, de 11 de julio, de Servicios de
            la Sociedad de la Información y del Comercio Electrónico (LSSI-CE), se informa que
            el titular del sitio web <strong>apizum.com</strong> es APIzum S.L., con domicilio
            social en Madrid, España, e inscrita en el Registro Mercantil de Madrid.
          </p>
        </section>

        <section class="static-page__section">
          <h2>2. Objeto y ámbito de aplicación</h2>
          <p>
            El presente Aviso Legal regula el acceso y uso del sitio web apizum.com, sus
            contenidos y servicios. El acceso y uso del sitio implica la aceptación plena y
            sin reservas de las presentes condiciones legales.
          </p>
        </section>

        <section class="static-page__section">
          <h2>3. Propiedad intelectual e industrial</h2>
          <p>
            Todos los contenidos del sitio web (textos, imágenes, diseño gráfico, código fuente,
            logotipos y demás elementos) son propiedad de APIzum S.L. o de sus proveedores de
            contenido, y están protegidos por las leyes españolas e internacionales de propiedad
            intelectual e industrial. Queda prohibida su reproducción, distribución o comunicación
            pública sin autorización expresa.
          </p>
        </section>

        <section class="static-page__section">
          <h2>4. Exclusión de garantías y responsabilidad</h2>
          <p>
            APIzum S.L. no se hace responsable de los daños o perjuicios de cualquier naturaleza
            derivados del uso del sitio web, de la información contenida en el mismo o de los
            servicios ofrecidos, incluyendo fallos técnicos o interrupciones del servicio.
          </p>
        </section>

        <section class="static-page__section">
          <h2>5. Legislación aplicable y jurisdicción</h2>
          <p>
            Las presentes condiciones legales se rigen por la legislación española. Para la
            resolución de cualquier controversia derivada del acceso o uso del sitio web, las
            partes se someten a los Juzgados y Tribunales de Madrid, renunciando expresamente
            a cualquier otro fuero que pudiera corresponderles.
          </p>
        </section>

        <section class="static-page__section">
          <h2>6. Modificaciones</h2>
          <p>
            APIzum S.L. se reserva el derecho a modificar el presente Aviso Legal en cualquier
            momento. Los cambios entrarán en vigor desde su publicación en el sitio web.
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
