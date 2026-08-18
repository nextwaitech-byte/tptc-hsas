import { Injectable, inject, signal } from '@angular/core';
import { catchError, of, tap } from 'rxjs';
import { ApiService } from './api.service';

export interface Branding {
  app_name: string;
  app_short: string;
  organization: string;
  partners: string;
  tagline: string;
  login_subtitle: string;
  support_line: string;
  has_custom_logo: boolean;
  logo_url: string;
  logo_updated_at: number;
}

const FALLBACK: Branding = {
  app_name: 'Human Security Assessment System',
  app_short: 'HSAS',
  organization: 'TPTC',
  partners: 'UNDP · UNTFHS',
  tagline: 'Secure border assessment for western Tanzania — operational picture, intelligence reporting, and HQ decision support.',
  login_subtitle: 'Sign in to HSAS to continue',
  support_line: 'Kigoma · Bukoba · Katavi · Rukwa',
  has_custom_logo: false,
  logo_url: '/api/v1/branding/logo',
  logo_updated_at: 0
};

@Injectable({ providedIn: 'root' })
export class BrandingService {
  private readonly api = inject(ApiService);
  readonly branding = signal<Branding>(FALLBACK);

  load() {
    return this.api.get<Branding>('/branding').pipe(
      tap(b => this.apply(b)),
      catchError(() => of(this.branding()))
    );
  }

  apply(partial: Partial<Branding>): void {
    const next = { ...this.branding(), ...partial };
    this.branding.set(next);
    document.title = `${next.app_short} | ${next.organization}`;
  }

  logoSrc(): string {
    const b = this.branding();
    return `${b.logo_url}?v=${b.logo_updated_at || 0}`;
  }
}
