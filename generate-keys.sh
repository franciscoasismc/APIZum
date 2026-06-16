#!/bin/bash
# ================================================================
# generate-keys.sh — Genera el par de claves RSA para JWT
# ================================================================
# Uso: bash generate-keys.sh
# Requiere: openssl instalado
#
# Las claves se guardan en:
#   APIZum/APIZum/src/main/resources/certs/private.pem
#   APIZum/APIZum/src/main/resources/certs/public.pem
#
# ¡NUNCA subas estas claves al repositorio!
# El directorio certs/ está en .gitignore.
# ================================================================

set -e

CERTS_DIR="APIZum/APIZum/src/main/resources/certs"
mkdir -p "$CERTS_DIR"

echo "🔑 Generando clave privada RSA 2048..."
openssl genrsa -out "$CERTS_DIR/private.pem" 2048

echo "🔓 Extrayendo clave pública..."
openssl rsa -in "$CERTS_DIR/private.pem" -pubout -out "$CERTS_DIR/public.pem"

echo "✅ Claves generadas en $CERTS_DIR/"
echo "   private.pem → mantener en secreto"
echo "   public.pem  → puede compartirse"
