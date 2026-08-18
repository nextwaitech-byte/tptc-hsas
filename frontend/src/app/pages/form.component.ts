import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { CATALOG, Field } from '../core/catalog';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-form',
  imports: [FormsModule, RouterLink],
  template: `
<div class="page-header"><div><h5 class="fw-bold mb-0">{{ meta.createLabel || 'New record' }}</h5><p>{{ meta.title }}</p></div>
  <a class="btn btn-outline-secondary" [routerLink]="'/' + key">Back</a>
</div>
@if (error) { <div class="alert alert-danger">{{ error }}</div> }
@if (auth.user()?.is_super_admin && key === 'intelligence') {
  <div class="alert alert-info">HQ admin is view/analysis only. Field officers submit reports.</div>
}
<div class="panel form-card"><div class="panel-body">
  <form (ngSubmit)="save()" class="row g-3">
    @for (f of meta.fields || []; track f.key) {
      <div class="col-md-6" [class.col-md-12]="f.type === 'textarea'">
        <label class="form-label small fw-semibold">{{ f.label }}</label>
        @if (f.type === 'textarea') {
          <textarea class="form-control" rows="4" [(ngModel)]="model[f.key]" [name]="f.key"></textarea>
        } @else if (f.type === 'select') {
          <select class="form-select" [(ngModel)]="model[f.key]" [name]="f.key">
            @for (o of f.options; track o) { <option [value]="o">{{ o }}</option> }
          </select>
        } @else {
          <input class="form-control" [type]="f.type === 'number' ? 'number' : 'text'" [(ngModel)]="model[f.key]" [name]="f.key">
        }
      </div>
    }
    <div class="col-12"><button class="btn btn-primary" [disabled]="saving">{{ saving ? 'Saving...' : 'Submit' }}</button></div>
  </form>
</div></div>
`
})
export class FormComponent {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly auth = inject(AuthService);
  key = 'intelligence';
  meta = CATALOG['intelligence'];
  model: Record<string, any> = {};
  saving = false;
  error = '';

  constructor() {
    this.route.data.subscribe(d => {
      this.key = d['key'];
      this.meta = CATALOG[this.key];
      (this.meta.fields || []).forEach((f: Field) => {
        if (f.options?.length) this.model[f.key] = f.options[0];
      });
    });
    this.route.queryParamMap.subscribe(q => {
      if (q.get('domain')) this.model['domain'] = q.get('domain');
    });
    const postId = this.auth.user()?.border_post_id;
    if (postId) this.model['borderPostId'] = postId;
  }

  save(): void {
    this.saving = true;
    this.error = '';
    const body = { ...this.model };
    if (body['hasCargo'] !== undefined) body['hasCargo'] = body['hasCargo'] === 'true' || body['hasCargo'] === true;
    if (body['cargoDeclared'] !== undefined) body['cargoDeclared'] = body['cargoDeclared'] === 'true' || body['cargoDeclared'] === true;
    this.api.post(this.meta.endpoint, body).subscribe({
      next: () => this.router.navigateByUrl('/' + this.key),
      error: err => {
        this.error = err.error?.error || 'Save failed';
        this.saving = false;
      }
    });
  }
}
