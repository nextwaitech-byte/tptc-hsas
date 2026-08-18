import { Component, inject, OnInit } from '@angular/core';
import { ApiService } from '../core/api.service';

@Component({
  selector: 'app-cop',
  template: `
<div class="page-header"><div><h5 class="fw-bold mb-0">Common Operational Picture</h5><p>HQ summarized intelligence for western Tanzania</p></div></div>
@if (cop) {
  <div class="cop-hero mb-3">
    <span class="threat-chip">NATIONAL THREAT</span>
    <h2 class="fw-bold mt-2">{{ cop.threat_label }}</h2>
    <div class="d-flex flex-wrap gap-2 mt-3">
      <div class="cop-metric"><strong class="fs-4 d-block">{{ cop.open_intelligence }}</strong>Open intel</div>
      <div class="cop-metric"><strong class="fs-4 d-block">{{ cop.critical_intelligence }}</strong>Critical</div>
      <div class="cop-metric"><strong class="fs-4 d-block">{{ cop.refugee_population }}</strong>Camp pop.</div>
      <div class="cop-metric"><strong class="fs-4 d-block">{{ cop.refugee_entries_30d }}</strong>Refugee 30d</div>
    </div>
  </div>
  <div class="row">
    <div class="col-lg-6">
      <div class="panel"><div class="panel-header"><h6><i class="bi bi-lightning-charge"></i> Recommended actions</h6></div>
        <div class="panel-body">
          @for (a of cop.recommended_actions || []; track a.title) {
            <div class="action-tile"><strong>{{ a.title }}</strong><span>{{ a.recommended_action }}</span></div>
          }
        </div>
      </div>
    </div>
    <div class="col-lg-6">
      <div class="panel"><div class="panel-header"><h6><i class="bi bi-binoculars"></i> Recent intelligence</h6></div>
        <div class="panel-body">
          @for (r of cop.recent_intelligence || []; track r.id) {
            <div class="intel-row"><span class="badge text-bg-secondary me-1">{{ r.domain }}</span>{{ r.title }}</div>
          }
        </div>
      </div>
    </div>
  </div>
}
`
})
export class CopComponent implements OnInit {
  private readonly api = inject(ApiService);
  cop: any;
  ngOnInit(): void { this.api.get<any>('/cop').subscribe(c => this.cop = c); }
}
