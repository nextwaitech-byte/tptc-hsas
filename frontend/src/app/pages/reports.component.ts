import { Component, inject } from '@angular/core';
import { ApiService } from '../core/api.service';
import { BrandingService } from '../core/branding.service';

@Component({
  selector: 'app-reports',
  template: `
<div class="page-header"><div><h5 class="fw-bold mb-0">Daily intelligence report</h5><p>Generated from live API data</p></div></div>
@if (report) {
  <div class="report-sheet">
    <div class="d-flex justify-content-between flex-wrap gap-2 mb-3">
      <div>
        <div class="report-kicker">{{ branding.branding().organization }} · {{ branding.branding().app_short }}</div>
        <h5 class="fw-bold mb-0">Daily intelligence briefing</h5>
      </div>
      <div class="text-muted small">Generated {{ report.generated_at }}</div>
    </div>
    <div class="stat-grid">
      <div class="stat-card stat-primary"><div class="stat-body"><div class="stat-value">{{ report.kpis.entries_today }}</div><div class="stat-label">Entries</div></div></div>
      <div class="stat-card stat-danger"><div class="stat-body"><div class="stat-value">{{ report.kpis.open_alerts }}</div><div class="stat-label">Alerts</div></div></div>
      <div class="stat-card stat-warning"><div class="stat-body"><div class="stat-value">{{ report.cop.threat_label }}</div><div class="stat-label">Threat</div></div></div>
    </div>
    <h6 class="mt-3">Recent movements</h6>
    <div class="table-responsive">
      <table class="table app-table table-sm mb-0"><thead><tr><th>Code</th><th>Nationality</th><th>Purpose</th><th>Risk</th></tr></thead>
        <tbody>@for (m of report.movements; track m.id) { <tr><td>{{ m.record_code }}</td><td>{{ m.nationality }}</td><td>{{ m.purpose }}</td><td>{{ m.risk_score }}</td></tr> }</tbody>
      </table>
    </div>
  </div>
}
`
})
export class ReportsComponent {
  private readonly api = inject(ApiService);
  readonly branding = inject(BrandingService);
  report: any;
  constructor() { this.api.get<any>('/reports/daily').subscribe(r => this.report = r); }
}
