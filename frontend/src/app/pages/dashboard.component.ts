import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { BrandingService } from '../core/branding.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  template: `
<div class="welcome-banner">
  <div class="d-flex gap-3 align-items-start">
    <img [src]="branding.logoSrc()" [alt]="branding.branding().organization" width="48" height="48" class="banner-logo">
    <div>
    <h5>Welcome, {{ firstName() }}!</h5>
    <p>{{ branding.branding().app_name || auth.user()?.app_name }} — {{ auth.user()?.is_post_officer ? 'post operations desk' : 'live border assessment' }}</p>
    @if (!auth.user()?.is_post_officer) {
      <a routerLink="/modules" class="btn btn-sm btn-light mt-2"><i class="bi bi-grid-3x3-gap me-1"></i> Open Data Matrix (16 modules)</a>
    }
    </div>
  </div>
  <div class="d-flex gap-3">
    <div class="banner-stat"><strong>{{ kpis['entries_today'] || 0 }}</strong><span>Entries Today</span></div>
    @if (!auth.user()?.is_post_officer) {
      <div class="banner-stat"><strong>{{ kpis['open_alerts'] || 0 }}</strong><span>Open Alerts</span></div>
    }
    <div class="banner-stat"><strong>{{ kpis['vehicles_today'] || 0 }}</strong><span>Vehicles Today</span></div>
  </div>
</div>

@if (cop) {
  <div class="section-title d-flex justify-content-between"><span><i class="bi bi-radar"></i> Common Operational Picture</span>
    <a routerLink="/cop" class="btn btn-sm btn-outline-primary">Full COP</a>
  </div>
  <div class="chart-card cop-hero mb-3">
    <div class="d-flex flex-wrap justify-content-between gap-3">
      <div>
        <span class="threat-chip">NATIONAL THREAT</span>
        <h4 class="fw-bold mb-0 mt-2">{{ threatLabel() }}</h4>
      </div>
      <div class="d-flex flex-wrap gap-2 text-center">
        <div class="cop-metric"><strong class="d-block fs-5">{{ cop.border_incidents }}</strong><small>Incidents</small></div>
        <div class="cop-metric"><strong class="d-block fs-5">{{ cop.disease_outbreak_alerts }}</strong><small>Disease</small></div>
        <div class="cop-metric"><strong class="d-block fs-5">{{ cop.humanitarian_situation }}</strong><small>Humanitarian</small></div>
        <div class="cop-metric"><strong class="d-block fs-5">{{ cop.infrastructure_disruptions }}</strong><small>Infrastructure</small></div>
        <div class="cop-metric"><strong class="d-block fs-5">{{ cop.weather_hazards }}</strong><small>Weather</small></div>
        <div class="cop-metric"><strong class="d-block fs-5">{{ cop.refugee_population }}</strong><small>Camp pop.</small></div>
      </div>
    </div>
  </div>
}

<div class="section-title"><i class="bi bi-people"></i> People & Migration</div>
<div class="stat-grid">
  <div class="stat-card stat-primary"><div class="stat-icon"><i class="bi bi-box-arrow-in-right"></i></div><div class="stat-body"><div class="stat-value">{{ kpis['entries_today'] || 0 }}</div><div class="stat-label">Entries Today</div></div></div>
  <div class="stat-card stat-info"><div class="stat-icon"><i class="bi bi-box-arrow-right"></i></div><div class="stat-body"><div class="stat-value">{{ kpis['exits_today'] || 0 }}</div><div class="stat-label">Exits Today</div></div></div>
  <div class="stat-card stat-warning"><div class="stat-icon"><i class="bi bi-exclamation-triangle"></i></div><div class="stat-body"><div class="stat-value">{{ kpis['flagged_count'] || 0 }}</div><div class="stat-label">Flagged</div></div></div>
  <div class="stat-card stat-danger"><div class="stat-icon"><i class="bi bi-bell"></i></div><div class="stat-body"><div class="stat-value">{{ kpis['open_alerts'] || 0 }}</div><div class="stat-label">Open Alerts</div></div></div>
  <div class="stat-card stat-success"><div class="stat-icon"><i class="bi bi-truck"></i></div><div class="stat-body"><div class="stat-value">{{ kpis['vehicles_today'] || 0 }}</div><div class="stat-label">Vehicles Today</div></div></div>
  <div class="stat-card stat-purple"><div class="stat-icon"><i class="bi bi-shield"></i></div><div class="stat-body"><div class="stat-value">{{ kpis['open_incidents'] || 0 }}</div><div class="stat-label">Open Incidents</div></div></div>
</div>
`
})
export class DashboardComponent implements OnInit, OnDestroy {
  readonly auth = inject(AuthService);
  readonly branding = inject(BrandingService);
  private readonly api = inject(ApiService);
  kpis: any = {};
  cop: any = null;
  private timer?: number;

  ngOnInit(): void {
    this.refresh();
    this.timer = window.setInterval(() => this.refresh(), 10000);
  }
  ngOnDestroy(): void { if (this.timer) clearInterval(this.timer); }
  refresh(): void {
    this.api.get<any>('/analytics/live').subscribe(d => {
      this.kpis = d.kpis || {};
      this.cop = d.cop;
    });
  }
  firstName(): string { return (this.auth.user()?.name || '').split(' ')[0]; }
  threatLabel(): string {
    const map: any = { green: 'LOW', yellow: 'ELEVATED', orange: 'HIGH', red: 'CRITICAL' };
    return map[this.cop?.threat_level] || 'ELEVATED';
  }
}
