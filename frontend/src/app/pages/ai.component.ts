import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';

@Component({
  selector: 'app-ai',
  imports: [FormsModule],
  template: `
<div class="page-header"><div><h5 class="fw-bold mb-0">AI Analytics</h5><p>Rule-based situation room — patterns, anomalies, predictions</p></div></div>
@if (data) {
  <div class="ai-hero mb-3">
    <div class="d-flex justify-content-between flex-wrap gap-2">
      <span class="threat-chip"><i class="bi bi-cpu me-1"></i> SITUATION ROOM</span>
      <span class="ai-conf">Confidence {{ data.confidence }}%</span>
    </div>
    <h5 class="mt-3 mb-2">Executive summary</h5>
    <p class="mb-1" style="opacity:.92;max-width:70ch">{{ data.summary }}</p>
    <div class="small" style="opacity:.65">Generated {{ data.generated_at }}</div>
  </div>
  <div class="row g-3">
    <div class="col-lg-6">
      <div class="panel h-100"><div class="panel-header"><h6><i class="bi bi-exclamation-triangle"></i> Anomalies</h6></div>
        <div class="panel-body">
          @for (a of data.anomalies; track a.title) {
            <div class="intel-row"><span class="badge text-bg-danger me-2">{{ a.severity }}</span><strong>{{ a.title }}</strong> — {{ a.message }}</div>
          }
          @if (!data.anomalies?.length) { <div class="empty-hint">No anomalies detected.</div> }
        </div>
      </div>
    </div>
    <div class="col-lg-6">
      <div class="panel h-100"><div class="panel-header"><h6><i class="bi bi-lightbulb"></i> Recommendations</h6></div>
        <div class="panel-body">
          @for (r of data.recommendations; track r) { <div class="action-tile">{{ r }}</div> }
        </div>
      </div>
    </div>
  </div>
}
<div class="panel mt-3"><div class="panel-header"><h6><i class="bi bi-chat-dots"></i> Ask the assistant</h6></div>
  <div class="panel-body">
    <form class="d-flex gap-2" (ngSubmit)="ask()">
      <input class="form-control" [(ngModel)]="question" name="q" placeholder="Ask about threat, movements, health...">
      <button class="btn btn-primary">Ask</button>
    </form>
    @if (answer) { <div class="answer-card mt-3">{{ answer }}</div> }
  </div>
</div>
`
})
export class AiComponent implements OnInit {
  private readonly api = inject(ApiService);
  data: any;
  question = '';
  answer = '';
  ngOnInit(): void { this.api.get<any>('/ai/analysis').subscribe(d => this.data = d); }
  ask(): void {
    this.api.post<any>('/ai/ask', { question: this.question }).subscribe(r => this.answer = r.answer);
  }
}
