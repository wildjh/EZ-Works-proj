# EZWorks Frontend (Angular 19)

Cliente web para la API en Railway.

**API:** https://ezworks-backend-production.up.railway.app

## Requisitos

- Node.js 20+
- npm

## Desarrollo

```bash
cd ezworks-frontend
npm install
npm start
```

Abre http://localhost:4200

## Build producción

```bash
npm run build
```

Salida en `dist/ezworks-frontend/browser/`

## Flujos incluidos

| Rol | Pantallas |
|-----|-----------|
| Todos | Login, registro, inicio, perfil |
| Empleador | Mis requerimientos, crear, detalle, postulaciones, match |
| Ayudante | Vacantes, postular |
| Match | Chat por conversación |

## Configurar otra API

Edita `src/environments/environment.ts` y `environment.production.ts`:

```typescript
apiUrl: 'https://tu-backend.up.railway.app',
```
