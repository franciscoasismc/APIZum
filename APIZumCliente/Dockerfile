# ══════════════════════════════════════════════════════════════════
# Dockerfile — Frontend APIZum
# ══════════════════════════════════════════════════════════════════
# Construcción multietapa:
#   Etapa 1 (build)  → instala dependencias con pnpm y genera los
#                      archivos estáticos de producción con Vite
#   Etapa 2 (runtime)→ sirve los archivos con Nginx (imagen Alpine)
#
# Uso:
#   docker build -t apizum-frontend .
#   docker run -p 80:80 apizum-frontend
# ══════════════════════════════════════════════════════════════════


# Etapa 1: Compilación
FROM node:20-alpine AS build

WORKDIR /app

# Instalamos pnpm globalmente (el proyecto usa pnpm como gestor de paquetes)
RUN npm install -g pnpm

# Copiamos los descriptores de dependencias primero para aprovechar
# la caché de Docker.
COPY package.json pnpm-lock.yaml ./

# Instalamos las dependencias del proyecto
RUN pnpm install --frozen-lockfile

# Copiamos el resto del código fuente
COPY . .

# Generamos el build de producción (resultado en /app/dist)
RUN pnpm run build


# Etapa 2: Imagen de ejecución
# Nginx Alpine sirve archivos estáticos.
FROM nginx:alpine

# Copiamos el bundle de Vite al directorio raíz de Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Copiamos nuestra configuración personalizada de Nginx:
#   - Manejo de Vue Router (history mode → siempre devuelve index.html)
#   - Proxy inverso de /api → backend Spring Boot
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Puerto HTTP estándar
EXPOSE 80

# Nginx arranca en primer plano
CMD ["nginx", "-g", "daemon off;"]
