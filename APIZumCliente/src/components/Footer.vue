<template>
  <footer class="footer">
    <div class="footer__inner">

      <!-- Columna 1: logo + copyright -->
      <div class="footer__brand">
        <img src="/images/APIzum.webp" alt="APIzum logo" class="footer__logo" />
        <p class="footer__brand-name">APIzum</p>
        <p class="footer__copyright">
          Copyright &copy; {{ new Date().getFullYear() }}. Todos los derechos reservados.
        </p>
      </div>

      <!-- Columna 2: enlaces -->
      <nav class="footer__links" aria-label="Páginas">
        <RouterLink to="/sobre-nosotros">Sobre APIzum</RouterLink>
        <RouterLink to="/contacto">Contacto</RouterLink>
        <RouterLink to="/aviso-legal">Aviso legal</RouterLink>
        <RouterLink to="/privacidad">Privacidad</RouterLink>
      </nav>

      <!-- Columna 3: redes sociales -->
      <nav class="footer__social" aria-label="Redes sociales">
        <a href="https://instagram.com" target="_blank" rel="noopener" aria-label="Instagram">
          <img src="/icons/instagram.svg" alt="Instagram" class="footer__icon" />
        </a>
        <a href="https://twitter.com" target="_blank" rel="noopener" aria-label="Twitter">
          <img src="/icons/twitter.svg" alt="Twitter" class="footer__icon" />
        </a>
        <a href="https://whatsapp.com" target="_blank" rel="noopener" aria-label="WhatsApp">
          <img src="/icons/whatsapp.svg" alt="WhatsApp" class="footer__icon" />
        </a>
      </nav>

    </div>
  </footer>
</template>
