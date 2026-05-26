import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RolCodigo } from '../models/api.models';

export const roleGuard = (role: RolCodigo): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.hasRole(role)) {
    return true;
  }
  return router.createUrlTree(['/inicio']);
};
