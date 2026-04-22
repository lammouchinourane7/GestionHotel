import { HttpInterceptorFn } from '@angular/common/http';

import { API_BASE_URL } from '../constants/api.constants';

export const apiPrefixInterceptor: HttpInterceptorFn = (req, next) => {
  const isAbsoluteUrl = /^https?:\/\//i.test(req.url);

  if (isAbsoluteUrl) {
    return next(req);
  }

  const normalizedUrl = req.url.startsWith('/') ? req.url : `/${req.url}`;
  return next(
    req.clone({
      url: `${API_BASE_URL}${normalizedUrl}`
    })
  );
};
