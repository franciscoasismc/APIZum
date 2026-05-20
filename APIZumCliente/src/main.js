/**
 * main.js — Punto de entrada de la aplicación Vue
 * =================================================
 * Crea la instancia de Vue, registra el router para la navegación
 * entre páginas y monta la aplicación en el elemento #app del HTML.
 * También importa el CSS global que aplica estilos a toda la app.
 */

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/main.css'

// Crea la app, conecta el router y la monta en el <div id="app"> del index.html
createApp(App).use(router).mount('#app')
