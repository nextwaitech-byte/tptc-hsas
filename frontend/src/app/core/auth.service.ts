import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { ApiService } from './api.service';

export interface AuthUser {
  id: number;
  name: string;
  email: string;
  role_name: string;
  role_label: string;
  border_post_id: number | null;
  border_post_name: string | null;
  permissions: string[];
  is_post_officer: boolean;
  is_super_admin: boolean;
  app_name: string;
  app_short: string;
  organization?: string;
  logo_url?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  readonly user = signal<AuthUser | null>(this.readUser());

  token(): string | null {
    return localStorage.getItem('hsas_token');
  }

  isLoggedIn(): boolean {
    return !!this.token() && !!this.user();
  }

  has(perm: string): boolean {
    return this.user()?.permissions?.includes(perm) ?? false;
  }

  login(email: string, password: string) {
    return this.api.post<{ token: string; user: AuthUser }>('/auth/login', { email, password }).pipe(
      tap(res => {
        localStorage.setItem('hsas_token', res.token);
        localStorage.setItem('hsas_user', JSON.stringify(res.user));
        this.user.set(res.user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('hsas_token');
    localStorage.removeItem('hsas_user');
    this.user.set(null);
    this.router.navigateByUrl('/login');
  }

  patchUser(partial: Partial<AuthUser>): void {
    const current = this.user();
    if (!current) return;
    const next = { ...current, ...partial };
    localStorage.setItem('hsas_user', JSON.stringify(next));
    this.user.set(next);
  }

  private readUser(): AuthUser | null {
    const raw = localStorage.getItem('hsas_user');
    return raw ? JSON.parse(raw) as AuthUser : null;
  }
}
