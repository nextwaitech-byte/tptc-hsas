import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { CATALOG } from '../core/catalog';

@Component({
  selector: 'app-records',
  imports: [RouterLink],
  template: `
<div class="page-header">
  <div><h5 class="fw-bold mb-0">{{ meta.title }}</h5><p>{{ meta.subtitle }}</p></div>
  @if (meta.createPath && auth.has(meta.createPerm || '')) {
    <a class="btn btn-primary" [routerLink]="meta.createPath" [queryParams]="query"><i class="bi bi-plus-lg me-1"></i> {{ meta.createLabel }}</a>
  }
</div>
<div class="panel table-panel"><div class="panel-body table-responsive p-0">
  <table class="table app-table align-middle mb-0">
    <thead><tr>@for (c of meta.columns; track c.key) { <th>{{ c.label }}</th> } @if (meta.actions) { <th></th> }</tr></thead>
    <tbody>
      @for (row of rows; track row.id || row.indicator_code) {
        <tr>
          @for (c of meta.columns; track c.key) {
            <td>
              @if (c.badge) { <span class="badge" [class]="badgeClass(row[c.key])">{{ row[c.key] }}</span> }
              @else { {{ row[c.key] }} }
            </td>
          }
          @if (meta.actions) {
            <td class="text-end">
              @if (key === 'alerts' && row.status === 'open') {
                <button class="btn btn-sm btn-outline-secondary me-1" (click)="patch('/alerts/' + row.id + '/acknowledge')">Ack</button>
                <button class="btn btn-sm btn-outline-success" (click)="patch('/alerts/' + row.id + '/resolve')">Resolve</button>
              }
            </td>
          }
        </tr>
      }
      @if (!rows.length) { <tr><td [attr.colspan]="meta.columns.length + 1" class="empty-hint py-5">No records yet.</td></tr> }
    </tbody>
  </table>
</div></div>
`
})
export class RecordsComponent {
  readonly auth = inject(AuthService);
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  key = 'intelligence';
  rows: any[] = [];
  query: Record<string, string> = {};
  meta = CATALOG['intelligence'];

  constructor() {
    this.route.data.subscribe(d => {
      this.key = d['key'];
      this.meta = CATALOG[this.key];
      this.route.queryParamMap.subscribe(q => {
        this.query = {};
        q.keys.forEach(k => this.query[k] = q.get(k) || '');
        this.load();
      });
    });
  }

  load(): void {
    const params = this.key === 'intelligence' ? { domain: this.query['domain'] } : undefined;
    this.api.get<any[]>(this.meta.endpoint, params).subscribe(r => this.rows = r || []);
  }

  patch(path: string): void {
    this.api.patch(path, {}).subscribe(() => this.load());
  }

  badgeClass(v: string): string {
    if (['critical', 'high', 'red', 'open', 'flagged', 'alert'].includes(v)) return 'text-bg-danger';
    if (['medium', 'orange', 'watch', 'acknowledged'].includes(v)) return 'text-bg-warning';
    if (['resolved', 'cleared', 'verified', 'green', 'low'].includes(v)) return 'text-bg-success';
    return 'text-bg-secondary';
  }
}
