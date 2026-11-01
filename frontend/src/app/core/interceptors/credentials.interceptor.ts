import { HttpInterceptorFn } from '@angular/common/http';

/** Send cookies (session + XSRF-TOKEN) on all API calls. */
export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith('/api/')) {
    return next(req.clone({ withCredentials: true }));
  }
  return next(req);
};
