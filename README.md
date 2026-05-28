# EZWorks Backend

API REST para la plataforma de contratación informal (Spring Boot 3 + MySQL + JWT).

## Requisitos

- **Java 17** (obligatorio para compilar; si tienes Java 26 por defecto, usa `export JAVA_HOME=/opt/homebrew/opt/openjdk@17`)
- Maven 3.9+
- Docker (opcional, para MySQL local)

```bash
# macOS Homebrew — forzar Java 17 en Maven
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
```

## Inicio rápido (local)

### 1. MySQL sin Docker (recomendado si no usas Docker)

**Instalar MySQL en macOS:**

```bash
brew install mysql
brew services start mysql
```

**Crear base de datos y usuario** (entra a MySQL como root; en Homebrew suele no tener contraseña al inicio):

```bash
mysql -u root
```

```sql
CREATE DATABASE ezworks CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'ezworks'@'localhost' IDENTIFIED BY 'ezworks123';
GRANT ALL PRIVILEGES ON ezworks.* TO 'ezworks'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**Crear las tablas:** arranca el backend una vez (Flyway aplica las migraciones automáticamente):

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
./run.sh
```

Cuando veas en consola algo como `Started EzworksApplication`, las tablas ya existen. Puedes dejar la app corriendo o detenerla con `Ctrl+C`.

**Ver la BD con un cliente gráfico** (elige uno):

| App | Descarga |
|-----|----------|
| [DBeaver](https://dbeaver.io) | Gratis, multiplataforma |
| [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) | Oficial Oracle |
| [TablePlus](https://tableplus.com) | macOS, muy usado |
| [Sequel Ace](https://sequel-ace.com) | Gratis, solo macOS |

Datos de conexión:

| Campo | Valor |
|-------|--------|
| Host | `localhost` |
| Puerto | `3306` |
| Base de datos | `ezworks` |
| Usuario | `ezworks` |
| Contraseña | `ezworks123` |

**Ver la BD por terminal:**

```bash
mysql -u ezworks -pezworks123 ezworks -e "SHOW TABLES;"
```

---

### 1b. MySQL con Docker (opcional)

```bash
cd ezworks-backend
docker compose up -d
```

Espera ~20 s a que MySQL esté listo (`docker compose ps`).

### 2. Variables de entorno (opcional)

Copia `.env.example` a `.env` o exporta:

```bash
export SPRING_PROFILES_ACTIVE=local
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=ezworks
export DB_USER=ezworks
export DB_PASSWORD=ezworks123
export JWT_SECRET=ezworks-dev-secret-change-in-production-min-32-chars!!
```

### 3. Ejecutar la API

```bash
./run.sh
# o: export JAVA_HOME=/opt/homebrew/opt/openjdk@17 && mvn spring-boot:run
```

- Health: http://localhost:8080/health  
- Actuator: http://localhost:8080/actuator/health  

Flyway crea las tablas y datos semilla (roles + categorías) al arrancar.

---

## Cómo probar (flujo MVP)

Usa **curl**, Postman o la colección que prefieras. Sustituye `TOKEN` por el `accessToken` del login.

### 1. Registro empleador

```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "empleador@test.com",
    "password": "password123",
    "nombre": "Ana",
    "apellido": "García",
    "telefono": "3001234567",
    "roles": ["EMPLEADOR"],
    "aceptaTerminos": true
  }' | jq
```

### 2. Registro ayudante

```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ayudante@test.com",
    "password": "password123",
    "nombre": "Luis",
    "apellido": "Pérez",
    "telefono": "3009876543",
    "roles": ["AYUDANTE"],
    "aceptaTerminos": true
  }' | jq
```

### 3. Login

```bash
TOKEN_EMP=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"empleador@test.com","password":"password123"}' | jq -r .accessToken)

TOKEN_AYU=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ayudante@test.com","password":"password123"}' | jq -r .accessToken)
```

### 4. Listar categorías (público)

```bash
curl -s http://localhost:8080/api/categorias | jq
```

### 5. Crear y publicar requerimiento (empleador)

```bash
REQ=$(curl -s -X POST http://localhost:8080/api/requerimientos \
  -H "Authorization: Bearer $TOKEN_EMP" \
  -H "Content-Type: application/json" \
  -d '{
    "categoriaId": 1,
    "titulo": "Pasear perro 1 hora",
    "descripcion": "Perro mediano, zona Centro",
    "remuneracion": 25000,
    "zonaAproximada": "Centro, Tunja"
  }' | jq -r .id)

curl -s -X POST "http://localhost:8080/api/requerimientos/$REQ/publicar" \
  -H "Authorization: Bearer $TOKEN_EMP" | jq
```

### 6. Ver vacantes y postular (ayudante)

```bash
curl -s http://localhost:8080/api/requerimientos/vacantes \
  -H "Authorization: Bearer $TOKEN_AYU" | jq

POST=$(curl -s -X POST "http://localhost:8080/api/requerimientos/$REQ/postulaciones" \
  -H "Authorization: Bearer $TOKEN_AYU" \
  -H "Content-Type: application/json" \
  -d '{"mensajePresentacion":"Tengo experiencia con mascotas"}' | jq -r .id)
```

### 7. Match (empleador)

```bash
MATCH=$(curl -s -X POST "http://localhost:8080/api/requerimientos/$REQ/match" \
  -H "Authorization: Bearer $TOKEN_EMP" \
  -H "Content-Type: application/json" \
  -d "{\"postulacionId\": $POST}" | jq)

echo $MATCH | jq
CONV_ID=$(echo $MATCH | jq -r .conversacionId)
```

### 8. Chat

```bash
curl -s -X POST "http://localhost:8080/api/conversaciones/$CONV_ID/mensajes" \
  -H "Authorization: Bearer $TOKEN_EMP" \
  -H "Content-Type: application/json" \
  -d '{"contenido":"Hola, nos vemos mañana a las 10"}' | jq

curl -s "http://localhost:8080/api/conversaciones/$CONV_ID" \
  -H "Authorization: Bearer $TOKEN_AYU" | jq
```

### 9. Perfil propio

```bash
curl -s http://localhost:8080/api/usuarios/me \
  -H "Authorization: Bearer $TOKEN_EMP" | jq
```

---

## Endpoints principales

| Método | Ruta | Auth | Rol |
|--------|------|------|-----|
| POST | `/api/auth/register` | No | — |
| POST | `/api/auth/login` | No | — |
| POST | `/api/auth/refresh` | No | — |
| GET | `/api/usuarios/me` | JWT | cualquiera |
| PATCH | `/api/usuarios/me` | JWT | cualquiera |
| GET | `/api/categorias` | No | — |
| POST | `/api/categorias` | JWT | ADMIN |
| GET | `/api/requerimientos/vacantes` | JWT | — |
| GET | `/api/requerimientos/mis` | JWT | EMPLEADOR |
| POST | `/api/requerimientos` | JWT | EMPLEADOR |
| POST | `/api/requerimientos/{id}/publicar` | JWT | EMPLEADOR |
| POST | `/api/requerimientos/{id}/postulaciones` | JWT | AYUDANTE |
| GET | `/api/requerimientos/{id}/postulaciones` | JWT | EMPLEADOR |
| POST | `/api/requerimientos/{id}/match` | JWT | EMPLEADOR |
| GET | `/api/conversaciones/{id}` | JWT | participante |
| POST | `/api/conversaciones/{id}/mensajes` | JWT | participante |

---

## Subir la base de datos a Railway

### Paso 1: Crear MySQL en Railway

1. Entra a [railway.app](https://railway.app) → **New Project**.
2. **Add Service** → **Database** → **MySQL**.
3. Espera a que el servicio esté activo.
4. Abre el servicio MySQL → pestaña **Variables**. Anota:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`

### Paso 2: Desplegar el backend en Railway

1. En el mismo proyecto: **Add Service** → **GitHub Repo** (conecta `ezworks-backend`) o **Empty Service** y despliega con CLI.
2. En el servicio **backend**, pestaña **Variables**, agrega:

| Variable | Valor |
|----------|--------|
| `SPRING_PROFILES_ACTIVE` | `railway` |
| `MYSQLHOST` | *(referencia al MySQL: `${{MySQL.MYSQLHOST}}`)* |
| `MYSQLPORT` | `${{MySQL.MYSQLPORT}}` |
| `MYSQLDATABASE` | `${{MySQL.MYSQLDATABASE}}` |
| `MYSQLUSER` | `${{MySQL.MYSQLUSER}}` |
| `MYSQLPASSWORD` | `${{MySQL.MYSQLPASSWORD}}` |
| `JWT_SECRET` | cadena aleatoria ≥ 32 caracteres |
| `PORT` | Railway lo inyecta automáticamente |

**Referenciar variables entre servicios (recomendado):**

En el servicio backend, usa la sintaxis de Railway:

```
MYSQLHOST=${{MySQL.MYSQLHOST}}
MYSQLPORT=${{MySQL.MYSQLPORT}}
MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
```

(Sustituye `MySQL` por el nombre exacto de tu servicio de base de datos en Railway.)

### Paso 3: Las tablas se crean solas (Flyway)

No hace falta importar el SQL a mano. Al arrancar el backend con perfil `railway`:

1. Flyway ejecuta `V1__mvp_schema.sql` y `V2__seed_categorias.sql`.
2. Quedan roles, categorías y estructura lista.

Si el deploy falla por conexión, revisa que el backend y MySQL estén en el **mismo proyecto** y que las variables apunten al host interno de Railway (no `localhost`).

### Paso 4: Verificar

```bash
curl https://TU-APP.up.railway.app/health
curl https://TU-APP.up.railway.app/api/categorias
```

### Alternativa: SQL manual en Railway

Si quieres ejecutar el script sin desplegar aún:

1. MySQL → **Connect** → abre **Railway CLI** o un cliente (TablePlus, DBeaver).
2. Conéctate con las credenciales del servicio.
3. Pega el contenido de `src/main/resources/db/migration/V1__mvp_schema.sql` y `V2__seed_categorias.sql`.

En producción es mejor dejar que **Flyway** lo haga al iniciar la app.

### Crear usuario ADMIN (manual)

Tras el primer deploy, crea un admin desde MySQL o un script:

```sql
-- 1) Crear usuario (password BCrypt de "admin123" — genera uno real con la app)
INSERT INTO usuario (email, password_hash, nombre, apellido, estado_cuenta)
VALUES ('admin@ezworks.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'EZWorks', 'ACTIVO');

-- 2) Asignar rol ADMIN (id=3 según seed)
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (LAST_INSERT_ID(), 3);
```

*(El hash de ejemplo corresponde a `password123`; en producción regístrate y asigna rol por SQL o endpoint futuro.)*

---

## Build JAR

```bash
mvn clean package -DskipTests
java -jar target/ezworks-backend-0.1.0-SNAPSHOT.jar
```

## Documentación relacionada

- [Diseño de BD](docs/DISENO-BASE-DE-DATOS.md)
- [Diagrama draw.io](docs/EZWorks-BD-Diagrama.drawio)
