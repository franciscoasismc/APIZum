# ══════════════════════════════════════════════════════════════════
# Dockerfile — Backend APIZum
# ══════════════════════════════════════════════════════════════════
# Construcción multietapa:
#   Etapa 1 (build)  → compila el proyecto con Gradle y genera el JAR
#   Etapa 2 (runtime)→ ejecuta el JAR con una imagen JRE mínima
#
# Uso:
#   docker build -t apizum-backend .
#   docker run -p 8081:8081 apizum-backend
# ══════════════════════════════════════════════════════════════════


# Etapa 1: Compilación
FROM gradle:8.14-jdk17 AS build

WORKDIR /app

# Copiamos primero los descriptores de dependencias para aprovechar
# la caché de Docker: si no cambian, no se re-descargan las librerías.
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Descargamos dependencias sin compilar el código fuente todavía
RUN gradle dependencies --no-daemon || true

# Copiamos el resto del código fuente y construimos el JAR ejecutable
COPY src ./src
RUN gradle bootJar --no-daemon


# Etapa 2: Imagen de ejecución
# Usamos una imagen Alpine mínima con solo el JRE (sin compilador)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos únicamente el JAR generado en la etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Puerto en el que escucha Spring Boot
EXPOSE 8081

# Variables de entorno con valores por defecto
ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/apizumbd \
    SPRING_DATASOURCE_USERNAME=root \
    SPRING_DATASOURCE_PASSWORD=rootpassword \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
