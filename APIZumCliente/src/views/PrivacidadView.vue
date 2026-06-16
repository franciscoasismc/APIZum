<template>
  <div class="page">
    <NavBar />

    <main class="page__content">
      <article class="static-page">
        <h1 class="static-page__title">Política de Privacidad</h1>

        <section class="static-page__section">
          <h2>1. Responsable del tratamiento</h2>
          <p>
            APIzum S.L. es la entidad responsable del tratamiento de los datos personales
            recabados a través del sitio web apizum.com. Puede contactar con nosotros en
            privacidad@apizum.com para cualquier cuestión relacionada con el tratamiento de
            sus datos.
          </p>
        </section>

        <section class="static-page__section">
          <h2>2. Datos que recopilamos</h2>
          <p>
            Recopilamos los datos que usted nos facilita directamente al registrarse o usar
            nuestros servicios: nombre, apellidos, dirección de correo electrónico, nombre de
            usuario y datos de cuenta bancaria virtual. No recabamos datos sensibles en el
            sentido del artículo 9 del RGPD.
          </p>
        </section>

        <section class="static-page__section">
          <h2>3. Finalidad y base legal del tratamiento</h2>
          <p>
            Tratamos sus datos con las siguientes finalidades: (a) gestión del registro y acceso
            a la plataforma —base legal: ejecución de contrato—; (b) prestación del servicio de
            transferencias —base legal: ejecución de contrato—; (c) cumplimiento de obligaciones
            legales —base legal: obligación legal—; (d) envío de comunicaciones de servicio
            —base legal: interés legítimo—.
          </p>
        </section>

        <section class="static-page__section">
          <h2>4. Conservación de los datos</h2>
          <p>
            Sus datos se conservarán durante el tiempo necesario para la prestación del servicio
            y durante los plazos legales aplicables. Una vez finalizada la relación, los datos
            serán bloqueados y eliminados conforme a la normativa vigente.
          </p>
        </section>

        <section class="static-page__section">
          <h2>5. Comunicación a terceros</h2>
          <p>
            Sus datos no serán cedidos a terceros, salvo obligación legal o cuando sea
            estrictamente necesario para la prestación del servicio (por ejemplo, entidades
            bancarias en el marco de la normativa de pagos).
          </p>
        </section>

        <section class="static-page__section">
          <h2>6. Sus derechos</h2>
          <p>
            Puede ejercer en cualquier momento sus derechos de acceso, rectificación, supresión,
            portabilidad, limitación y oposición al tratamiento dirigiéndose a
            privacidad@apizum.com, adjuntando copia de su documento de identidad. Tiene también
            derecho a presentar una reclamación ante la Agencia Española de Protección de Datos
            (www.aepd.es).
          </p>
        </section>

        <section class="static-page__section">
          <h2>7. Seguridad</h2>
          <p>
            Aplicamos medidas técnicas y organizativas adecuadas para garantizar la seguridad de
            sus datos personales, incluyendo cifrado en tránsito (TLS), cifrado asimétrico de
            contraseñas y control de acceso basado en roles (JWT).
          </p>
        </section>

        <section class="static-page__section">
          <h2>8. Cookies</h2>
          <p>
            Este sitio web únicamente utiliza cookies técnicas imprescindibles para el
            funcionamiento de la plataforma. No empleamos cookies de seguimiento ni publicidad.
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
