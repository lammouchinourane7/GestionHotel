import { inject } from '@angular/core';
import { CanActivateChildFn } from '@angular/router';

import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateChildFn = async (_route, state) => {
  const authService = inject(AuthService);

  if (!authService.isEnabled()) {
    return true;
  }

  if (authService.isAuthenticated()) {
    return true;
  }

  await authService.login(window.location.origin + state.url);
  return false;
};
