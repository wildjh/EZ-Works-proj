# Despliegue en Railway (EZWorks)

## Tu MySQL ya está en Railway

En el servicio **MySQL** no hace falta subir SQL manualmente. El **backend** ejecuta Flyway al arrancar.

### Variables del backend (pestaña Variables)

Usa **Variable Reference** al servicio MySQL (nombre del servicio en el panel, ej. `MySQL`):

| Variable | Valor |
|----------|--------|
| `SPRING_PROFILES_ACTIVE` | `railway` |
| `MYSQLHOST` | `${{MySQL.MYSQLHOST}}` |
| `MYSQLPORT` | `${{MySQL.MYSQLPORT}}` |
| `MYSQLDATABASE` | `${{MySQL.MYSQLDATABASE}}` |
| `MYSQLUSER` | `${{MySQL.MYSQLUSER}}` |
| `MYSQLPASSWORD` | `${{MySQL.MYSQLPASSWORD}}` |
| `JWT_SECRET` | clave aleatoria ≥ 32 caracteres |
| `NIXPACKS_JDK_VERSION` | `17` |

O en MySQL → **Connect** → botón **Add Variable Reference** al servicio backend.

**No uses** `localhost` ni `MYSQL_PUBLIC_URL` en el backend dentro de Railway; usa `mysql.railway.internal` vía `${{MySQL.MYSQLHOST}}`.

---

## Opción A — Dashboard (GitHub)

1. Sube el repo a GitHub (solo carpeta `ezworks-backend` o monorepo con root `ezworks-backend`).
2. Proyecto Railway → **Add Service** → **GitHub Repo**.
3. **Root Directory**: `ezworks-backend` (si el repo es la carpeta padre).
4. Pega las variables de la tabla arriba.
5. **Generate Domain** en Networking.
6. Deploy.

---

## Opción B — CLI (una terminal)

```bash
railway login
cd ezworks-backend
chmod +x scripts/deploy-railway.sh
./scripts/deploy-railway.sh
```

Luego en el dashboard: **Generate Domain** y prueba `/health`.

---

## Verificar

```bash
curl https://TU-DOMINIO.up.railway.app/health
curl https://TU-DOMINIO.up.railway.app/api/categorias
```

## Conectar DBeaver desde tu PC

Usa **MYSQL_PUBLIC_URL** del servicio MySQL (host `*.proxy.rlwy.net`, puerto público, no el 3306 interno).
