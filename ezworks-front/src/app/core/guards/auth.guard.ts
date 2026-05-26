import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.getToken()) {
    return router.createUrlTree(['/login']);
  }

  return auth.ensureSession().pipe(
    map((ok) => (ok ? true : router.createUrlTree(['/login'])))
  );
};

export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.getToken()) {
    auth.clearSession();
    return true;
  }

  return auth.ensureSession().pipe(
    map((ok) => (ok ? router.createUrlTree([auth.defaultHomeRoute()]) : true))
  );
};

/** Raíz `/` y rutas desconocidas: login si no hay sesión, inicio por rol si sí. */
export const rootRedirectGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.getToken()) {
    auth.clearSession();
    return router.createUrlTree(['/login']);
  }

  return auth.ensureSession().pipe(
    map((ok) =>
      ok ? router.createUrlTree([auth.defaultHomeRoute()]) : router.createUrlTree(['/login'])
    )
  );
};
