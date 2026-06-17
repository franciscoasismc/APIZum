# ══════════════════════════════════════════════════════════════════
# Dockerfile — Backend APIZum
# ══════════════════════════════════════════════════════════════════
# Construcción multietapa:
#   Etapa 1 (build)  → genera claves RSA, compila el JAR con Gradle
#   Etapa 2 (runtime)→ ejecuta el JAR con una imagen JRE mínima
# ══════════════════════════════════════════════════════════════════


# Etapa 1: Compilación
FROM gradle:8.14-jdk17 AS build

WORKDIR /app

COPY . .

# Genera claves RSA temporales para que Spring Security pueda arrancar.
# Se incrustan en el JAR; en producción real se usarían secretos externos.
RUN mkdir -p src/main/resources/certs && \
    openssl genrsa -out /tmp/private_rsa.pem 2048 && \
    openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \
        -in /tmp/private_rsa.pem \
        -out src/main/resources/certs/private.pem && \
    openssl rsa -in /tmp/private_rsa.pem -pubout \
        -out src/main/resources/certs/public.pem && \
    rm /tmp/private_rsa.pem

RUN JAVA_OPTS="-Xmx512m" gradle bootJar --no-daemon


# Etapa 2: Imagen de ejecución
# Usamos una imagen Alpine mínima con solo el JRE (sin compilador)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos únicamente el JAR generado en la etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Puerto en el que escucha Spring Boot
EXPOSE 8081

# Variables de entorno con valores por defecto para docker-compose local
# En Render/producción se sobreescriben desde el dashboard
ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/apizumbd \
    SPRING_DATASOURCE_USERNAME=root \
    SPRING_DATASOURCE_PASSWORD=rootpassword \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    CORS_ALLOWED_ORIGINS=http://localhost:5173

# Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
