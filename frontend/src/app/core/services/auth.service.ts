import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, switchMap, tap } from 'rxjs';
import { AdminApiService } from './admin-api.service';
import { LoginRequest } from '../models/api.models';

const AUTH_KEY = 'weddinglage-admin-authed';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly adminApi = inject(AdminApiService);
  readonly authenticated = signal(sessionStorage.getItem(AUTH_KEY) === '1');

  ensureCsrf(): Observable<string> {
    return this.adminApi.getCsrf().pipe(map((c) => c.token));
  }

  login(credentials: LoginRequest): Observable<boolean> {
    return this.ensureCsrf().pipe(
      switchMap(() => this.adminApi.login(credentials)),
      tap(() => {
        sessionStorage.setItem(AUTH_KEY, '1');
        this.authenticated.set(true);
      }),
      map(() => true),
      catchError(() => {
        sessionStorage.removeItem(AUTH_KEY);
        this.authenticated.set(false);
        return of(false);
      })
    );
  }

  logout(): Observable<boolean> {
    return this.ensureCsrf().pipe(
      switchMap(() => this.adminApi.logout()),
      tap(() => {
        sessionStorage.removeItem(AUTH_KEY);
        this.authenticated.set(false);
      }),
      map(() => true),
      catchError(() => {
        sessionStorage.removeItem(AUTH_KEY);
        this.authenticated.set(false);
        return of(true);
      })
    );
  }

  clearSession(): void {
    sessionStorage.removeItem(AUTH_KEY);
    this.authenticated.set(false);
  }
}
