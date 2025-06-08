import { HttpInterceptorFn } from '@angular/common/http';
import { HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { AuthService } from './auth/services/auth/auth.service';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<HttpEvent<any>> => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastr = inject(ToastrService);
  const token = authService.getToken();

  console.log(`Interceptor: URL=${req.url}, Token=${token ? token.substring(0, 20) + '...' : 'Missing'}`); // Debug

  if (token && !req.url.includes('/parking/api/auth/signin') && !req.url.includes('/parking/api/auth/signup')) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    console.log(`Interceptor: Added Authorization header for ${req.url}`); // Debug
    return next(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error(`Interceptor: Error for ${req.url}`, error);
        if (error.status === 401) {
          console.log('Interceptor: 401 Unauthorized, redirecting to login');
          toastr.error('Session expirée, veuillez vous reconnecter.', 'Erreur');
          authService.logout();
          router.navigate(['/auth']);
        }
        return throwError(() => error);
      })
    );
  }

  console.log(`Interceptor: No token or excluded URL for ${req.url}`); // Debug
  return next(req);
};