import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';

@Component({
  selector: 'app-modules',
  imports: [RouterLink],
  template: `
<div class="page-header"><div><h5 class="fw-bold mb-1">Data Matrix</h5><p>16 functional modules for western Tanzania human security assessment</p></div></div>
<div class="row g-3">
  @for (m of filtered(); track m.key) {
    <div class="col-md-6 col-xl-4">
      <div class="module-card-pro" [style.--mod-color]="m.color">
        <div class="d-flex align-items-center gap-2 mb-2">
          <span class="module-num" [style.background]="m.color">{{ m.number }}</span>
          <h6 class="mb-0 fw-bold">{{ m.label }}</h6>
        </div>
        <p class="text-muted small mb-3">{{ m.short }} · {{ m.type }}</p>
        <div class="d-flex flex-wrap gap-2">
            @if (m.intelDomain) {
              <a class="btn btn-sm btn-outline-primary" [routerLink]="['/intelligence']" [queryParams]="{domain:m.intelDomain}">View reports</a>
              <a class="btn btn-sm btn-primary" [routerLink]="['/intelligence/create']" [queryParams]="{domain:m.intelDomain}">New report</a>
            }
            @if (m.key === 'border') {
              <a class="btn btn-sm btn-outline-primary" routerLink="/movements">Movements</a>
              <a class="btn btn-sm btn-outline-primary" routerLink="/vehicles">Vehicles</a>
            }
            @if (m.key === 'community') { <a class="btn btn-sm btn-outline-primary" routerLink="/community">Community</a> }
            @if (m.key === 'capacity') { <a class="btn btn-sm btn-outline-primary" routerLink="/capacity">Capacity</a> }
            @if (m.key === 'gis') { <a class="btn btn-sm btn-outline-primary" routerLink="/gis">Map</a> }
            @if (m.key === 'ai') { <a class="btn btn-sm btn-outline-primary" routerLink="/ai">Situation Room</a> }
            @if (m.key === 'early_warning') { <a class="btn btn-sm btn-outline-primary" routerLink="/early-warning">Indicators</a> }
            @if (m.key === 'response') { <a class="btn btn-sm btn-outline-primary" routerLink="/incidents">Incidents</a> }
            @if (m.key === 'communications') { <a class="btn btn-sm btn-outline-primary" routerLink="/communications">Messages</a> }
            @if (m.key === 'administration') {
              <a class="btn btn-sm btn-outline-primary" routerLink="/users">Users</a>
              <a class="btn btn-sm btn-primary" routerLink="/settings">System settings</a>
            }
            @if (m.key === 'dashboard_outputs') { <a class="btn btn-sm btn-outline-primary" routerLink="/dashboard">Dashboard</a> }
          </div>
      </div>
    </div>
  }
</div>
`
})
export class ModulesComponent {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  modules: any[] = [];
  key = '';

  constructor() {
    this.api.get<any[]>('/modules').subscribe(m => this.modules = m);
    this.route.paramMap.subscribe(p => this.key = p.get('key') || '');
  }

  filtered(): any[] {
    return this.key ? this.modules.filter(m => m.key === this.key) : this.modules;
  }
}
