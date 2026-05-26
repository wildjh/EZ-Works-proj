import { Routes } from '@angular/router';
import { authGuard, guestGuard, rootRedirectGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    canActivate: [rootRedirectGuard],
    children: [],
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent),
    canActivate: [guestGuard],
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
    canActivate: [guestGuard],
  },
  {
    path: '',
    loadComponent: () =>
      import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      {
        path: 'inicio',
        loadComponent: () =>
          import('./features/home/home.component').then((m) => m.HomeComponent),
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/perfil/perfil.component').then((m) => m.PerfilComponent),
      },
      {
        path: 'empleador/requerimientos',
        loadComponent: () =>
          import('./features/empleador/mis-requerimientos/mis-requerimientos.component').then(
            (m) => m.MisRequerimientosComponent
          ),
        canActivate: [roleGuard('EMPLEADOR')],
      },
      {
        path: 'empleador/requerimientos/nuevo',
        loadComponent: () =>
          import('./features/empleador/requerimiento-form/requerimiento-form.component').then(
            (m) => m.RequerimientoFormComponent
          ),
        canActivate: [roleGuard('EMPLEADOR')],
      },
      {
        path: 'empleador/requerimientos/:id/editar',
        loadComponent: () =>
          import('./features/empleador/requerimiento-form/requerimiento-form.component').then(
            (m) => m.RequerimientoFormComponent
          ),
        canActivate: [roleGuard('EMPLEADOR')],
      },
      {
        path: 'empleador/requerimientos/:id',
        loadComponent: () =>
          import('./features/empleador/requerimiento-detalle/requerimiento-detalle.component').then(
            (m) => m.RequerimientoDetalleComponent
          ),
        canActivate: [roleGuard('EMPLEADOR')],
      },
      {
        path: 'empleador/ayudantes/:id',
        loadComponent: () =>
          import('./features/empleador/ayudante-perfil/ayudante-perfil.component').then(
            (m) => m.AyudantePerfilComponent
          ),
        canActivate: [roleGuard('EMPLEADOR')],
      },
      {
        path: 'ayudante/vacantes',
        loadComponent: () =>
          import('./features/ayudante/vacantes/vacantes.component').then((m) => m.VacantesComponent),
        canActivate: [roleGuard('AYUDANTE')],
      },
      {
        path: 'ayudante/vacantes/:id',
        loadComponent: () =>
          import('./features/ayudante/vacante-detalle/vacante-detalle.component').then(
            (m) => m.VacanteDetalleComponent
          ),
        canActivate: [roleGuard('AYUDANTE')],
      },
      {
        path: 'ayudante/empleadores/:id',
        loadComponent: () =>
          import('./features/ayudante/empleador-perfil/empleador-perfil.component').then(
            (m) => m.EmpleadorPerfilComponent
          ),
        canActivate: [roleGuard('AYUDANTE')],
      },
      {
        path: 'admin/usuarios',
        loadComponent: () =>
          import('./features/admin/admin-usuarios/admin-usuarios.component').then(
            (m) => m.AdminUsuariosComponent
          ),
        canActivate: [roleGuard('ADMIN')],
      },
      {
        path: 'chats',
        loadComponent: () =>
          import('./features/chats/chats-list/chats-list.component').then((m) => m.ChatsListComponent),
      },
      {
        path: 'chat/:id',
        loadComponent: () =>
          import('./features/chat/chat.component').then((m) => m.ChatComponent),
      },
    ],
  },
  { path: '**', canActivate: [rootRedirectGuard], children: [] },
];
