import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { ApiErrorResponse } from '../../models/api-error.model';

@Injectable({
  providedIn: 'root'
})
export class ErrorMessageService {
  resolve(error: unknown, fallback = 'Une erreur inattendue est survenue.'): string {
    if (!(error instanceof HttpErrorResponse)) {
      return fallback;
    }

    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }

    const apiError = error.error as ApiErrorResponse | null;
    if (!apiError) {
      return fallback;
    }

    if (apiError.validationErrors && Object.keys(apiError.validationErrors).length > 0) {
      return Object.values(apiError.validationErrors).join(' | ');
    }

    return apiError.message || fallback;
  }
}
