import { Component, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../core/auth.service';
import { BrandingService } from '../core/branding.service';

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
<aside class="app-sidebar">
  <div class="sidebar-brand">
    <div class="logo-wrap">
      <div class="logo-mark"><img [src]="branding.logoSrc()" [alt]="brand()?.organization || 'TPTC'"></div>
      <div>
        <h5>{{ brand()?.app_short || user()?.app_short || 'HSAS' }}</h5>
        <small>{{ user()?.is_post_officer ? 'Submit reports only' : (user()?.is_super_admin ? 'HQ view & analysis' : 'Human Security Assessment') }}</small>
      </div>
    </div>
  </div>
  <nav class="sidebar-nav">
    @if (user()?.is_post_officer) {
      <div class="nav-label">Submit Reports</div>
      <a class="nav-link" routerLink="/dashboard" routerLinkActive="active"><i class="bi bi-grid-3x3-gap nav-icon"></i> Report Types</a>
      <a class="nav-link" routerLink="/intelligence/create" [queryParams]="{domain:'population'}"><i class="bi bi-people-fill nav-icon"></i> 1. Population</a>
      <a class="nav-link" routerLink="/intelligence/create" [queryParams]="{domain:'security'}"><i class="bi bi-shield-exclamation nav-icon"></i> 2. Human Safety</a>
      <a class="nav-link" routerLink="/intelligence/create" [queryParams]="{domain:'economic'}"><i class="bi bi-currency-exchange nav-icon"></i> 3. Economic</a>
      <a class="nav-link" routerLink="/intelligence/create" [queryParams]="{domain:'health'}"><i class="bi bi-heart-pulse nav-icon"></i> 4. Health</a>
      <a class="nav-link" routerLink="/intelligence/create" [queryParams]="{domain:'environmental'}"><i class="bi bi-cloud-rain nav-icon"></i> 5. Disaster & Climate</a>
      <a class="nav-link" routerLink="/movements/create"><i class="bi bi-person-bounding-box nav-icon"></i> 6. Movement</a>
      <a class="nav-link" routerLink="/vehicles/create"><i class="bi bi-truck nav-icon"></i> 6. Vehicle & Cargo</a>
      <a class="nav-link" routerLink="/community/create"><i class="bi bi-chat-left-quote nav-icon"></i> 8. Community Report</a>
    } @else {
      <div class="nav-label">Command</div>
      <a class="nav-link" routerLink="/dashboard" routerLinkActive="active"><i class="bi bi-speedometer2 nav-icon"></i> Dashboard</a>
      <a class="nav-link" routerLink="/modules" routerLinkActive="active"><i class="bi bi-grid-3x3-gap nav-icon"></i> Data Matrix</a>
      @if (has('ai.view')) {
        <a class="nav-link" routerLink="/ai" routerLinkActive="active"><i class="bi bi-cpu nav-icon"></i> AI Analytics</a>
      }
      @if (has('cop.view')) {
        <a class="nav-link" routerLink="/cop" routerLinkActive="active"><i class="bi bi-radar nav-icon"></i> COP — HQ Picture</a>
      }
      <div class="nav-label">Assessment Domains</div>
      <a class="nav-link" routerLink="/modules/population"><i class="bi bi-people-fill nav-icon"></i> Population</a>
      <a class="nav-link" routerLink="/modules/human_safety"><i class="bi bi-shield-exclamation nav-icon"></i> Human Safety</a>
      <a class="nav-link" routerLink="/modules/economic"><i class="bi bi-currency-exchange nav-icon"></i> Economic</a>
      <a class="nav-link" routerLink="/modules/health"><i class="bi bi-heart-pulse nav-icon"></i> Health</a>
      <a class="nav-link" routerLink="/modules/disaster"><i class="bi bi-cloud-rain nav-icon"></i> Disaster & Climate</a>
      <a class="nav-link" routerLink="/modules/infrastructure"><i class="bi bi-building nav-icon"></i> Infrastructure</a>
      <div class="nav-label">Border & Community</div>
      <a class="nav-link" routerLink="/movements" routerLinkActive="active"><i class="bi bi-passport nav-icon"></i> Border & Migration</a>
      <a class="nav-link" routerLink="/community" routerLinkActive="active"><i class="bi bi-chat-left-quote nav-icon"></i> Community Reports</a>
      <a class="nav-link" routerLink="/capacity" routerLinkActive="active"><i class="bi bi-person-badge nav-icon"></i> Institutional Capacity</a>
      <a class="nav-link" routerLink="/incidents" routerLinkActive="active"><i class="bi bi-people nav-icon"></i> Response Coordination</a>
      <div class="nav-label">Warning & Spatial</div>
      <a class="nav-link" routerLink="/early-warning" routerLinkActive="active"><i class="bi bi-exclamation-octagon nav-icon"></i> Early Warning</a>
      <a class="nav-link" routerLink="/alerts" routerLinkActive="active"><i class="bi bi-bell nav-icon"></i> Alerts</a>
      <a class="nav-link" routerLink="/gis" routerLinkActive="active"><i class="bi bi-geo-alt-fill nav-icon"></i> GIS & Spatial</a>
      <a class="nav-link" routerLink="/communications" routerLinkActive="active"><i class="bi bi-megaphone nav-icon"></i> Public Comms</a>
      <div class="nav-label">System</div>
      <a class="nav-link" routerLink="/intelligence" routerLinkActive="active"><i class="bi bi-binoculars nav-icon"></i> All Intel Reports</a>
      <a class="nav-link" routerLink="/reports" routerLinkActive="active"><i class="bi bi-file-earmark-bar-graph nav-icon"></i> Reports</a>
      @if (has('users.manage')) {
        <a class="nav-link" routerLink="/users" routerLinkActive="active"><i class="bi bi-people nav-icon"></i> Users</a>
      }
      @if (has('settings.manage') || user()?.is_super_admin) {
        <a class="nav-link" routerLink="/settings" routerLinkActive="active"><i class="bi bi-gear-wide-connected nav-icon"></i> System Settings</a>
      }
    }
  </nav>
  <div class="sidebar-user">
    <div class="user-row">
      <div class="user-avatar">{{ initials() }}</div>
      <div>
        <div class="user-name">{{ user()?.name }}</div>
        <div class="user-role">{{ user()?.role_label }}</div>
      </div>
    </div>
    <button type="button" class="btn-logout" (click)="logout()"><i class="bi bi-box-arrow-right"></i> Sign Out</button>
  </div>
</aside>
<div class="app-main">
  <header class="app-topbar">
    <div class="topbar-title">
      <h4>{{ title }}</h4>
      <div class="breadcrumb-text">{{ brand()?.app_name || user()?.app_name }}
        @if (user()?.border_post_name) { <span> · {{ user()?.border_post_name }}</span> }
      </div>
    </div>
    <div class="topbar-actions">
      @if (user()?.is_post_officer) {
        <span class="badge text-bg-info">Submit only</span>
      } @else {
        @if (user()?.is_super_admin) { <span class="badge text-bg-secondary me-2">View & analysis</span> }
        <span class="live-pill"><span class="pulse-dot"></span> LIVE</span>
      }
    </div>
  </header>
  <main class="app-content"><router-outlet /></main>
</div>
`
})
export class ShellComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly branding = inject(BrandingService);
  user = this.auth.user;
  brand = this.branding.branding;
  title = 'Dashboard';

  constructor() {
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      const path = this.router.url.split('?')[0].split('/')[1] || 'dashboard';
      this.title = path.replace(/-/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
    });
  }

  has(p: string): boolean { return this.auth.has(p); }
  logout(): void { this.auth.logout(); }
  initials(): string {
    return (this.user()?.name || 'U').split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  }
}
