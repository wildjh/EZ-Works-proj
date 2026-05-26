#!/usr/bin/env bash
# Despliega EZWorks backend en Railway (mismo proyecto que tu MySQL).
# Requisitos: railway login  &&  npm i -g @railway/cli
set -euo pipefail

cd "$(dirname "$0")/.."

if ! railway whoami &>/dev/null; then
  echo "Primero ejecuta: railway login"
  exit 1
fi

echo "==> Enlaza este directorio a tu proyecto Railway (elige proyecto + servicio BACKEND, no MySQL)"
railway link

echo "==> Variables (referencias al servicio MySQL — cambia 'MySQL' si tu servicio tiene otro nombre)"
railway variables set \
  SPRING_PROFILES_ACTIVE=railway \
  'MYSQLHOST=${{MySQL.MYSQLHOST}}' \
  'MYSQLPORT=${{MySQL.MYSQLPORT}}' \
  'MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}' \
  'MYSQLUSER=${{MySQL.MYSQLUSER}}' \
  'MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}' \
  NIXPACKS_JDK_VERSION=17

if [[ -z "${JWT_SECRET:-}" ]]; then
  JWT_SECRET=$(openssl rand -base64 48 | tr -d '\n')
  echo "==> JWT_SECRET generado automáticamente"
fi
railway variables set "JWT_SECRET=${JWT_SECRET}"

echo "==> Desplegando..."
railway up --detach

echo ""
echo "Listo. Genera dominio en Railway: servicio backend → Settings → Networking → Generate Domain"
echo "Prueba: curl https://TU-DOMINIO/health"
