import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { BrandingService } from '../core/branding.service';

@Component({
  selector: 'app-settings',
  imports: [FormsModule],
  template: `
<div class="page-header">
  <div>
    <h5 class="fw-bold mb-1">System settings</h5>
    <p>Super Admin can change the logo, system name and other branding used across HSAS.</p>
  </div>
</div>

@if (error) { <div class="alert alert-danger">{{ error }}</div> }
@if (saved) { <div class="alert alert-success">Settings saved. The new name and logo now appear on login, sidebar and dashboard.</div> }

<div class="row g-3">
  <div class="col-lg-4">
    <div class="panel h-100">
      <div class="panel-header"><h6><i class="bi bi-image"></i> System logo</h6></div>
      <div class="panel-body">
        <div class="logo-preview-wrap mb-3">
          <img [src]="preview || branding.logoSrc()" alt="Logo preview">
        </div>
        <label class="btn btn-outline-primary w-100 mb-2">
          <i class="bi bi-upload me-1"></i> {{ uploading ? 'Uploading...' : 'Upload new logo' }}
          <input type="file" class="d-none" accept="image/png,image/jpeg,image/svg+xml,image/webp,image/gif" (change)="onLogo($event)" [disabled]="uploading">
        </label>
        @if (data?.has_custom_logo) {
          <button class="btn btn-outline-secondary w-100" type="button" (click)="resetLogo()" [disabled]="uploading">Restore default logo</button>
        }
        <p class="text-muted small mt-3 mb-0">PNG, JPG, SVG or WEBP. Maximum 2 MB. This logo is used on login, the sidebar and reports.</p>
      </div>
    </div>
  </div>
  <div class="col-lg-8">
    <div class="panel form-card">
      <div class="panel-header"><h6><i class="bi bi-sliders"></i> Names and branding</h6></div>
      <div class="panel-body">
        <form class="row g-3" (ngSubmit)="save()">
          @for (f of fields; track f.key) {
            <div [class]="f.multiline ? 'col-12' : 'col-md-6'">
              <label class="form-label small fw-semibold">{{ f.label }}</label>
              @if (f.multiline) {
                <textarea class="form-control" rows="3" [(ngModel)]="f.value" [name]="f.key"></textarea>
              } @else {
                <input class="form-control" [(ngModel)]="f.value" [name]="f.key">
              }
            </div>
          }
          <div class="col-12">
            <button class="btn btn-primary" [disabled]="saving">{{ saving ? 'Saving...' : 'Save settings' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
`
})
export class SettingsComponent {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  readonly branding = inject(BrandingService);
  data: any;
  fields: { key: string; label: string; value: string; multiline?: boolean }[] = [];
  saving = false;
  uploading = false;
  saved = false;
  error = '';
  preview = '';

  constructor() {
    this.api.get<any>('/settings').subscribe({
      next: d => this.apply(d),
      error: err => this.error = err.error?.error || 'Could not load settings.'
    });
  }

  save(): void {
    this.saving = true;
    this.error = '';
    this.saved = false;
    const body: Record<string, string> = {};
    this.fields.forEach(f => body[f.key] = f.value);
    this.api.put<any>('/settings', body).subscribe({
      next: d => {
        this.apply(d);
        this.saving = false;
        this.saved = true;
      },
      error: err => {
        this.error = err.error?.error || 'Save failed.';
        this.saving = false;
      }
    });
  }

  onLogo(ev: Event): void {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file) return;
    this.preview = URL.createObjectURL(file);
    this.uploading = true;
    this.error = '';
    this.api.upload<any>('/settings/logo', file).subscribe({
      next: d => {
        this.apply(d);
        this.uploading = false;
        this.saved = true;
        this.preview = '';
      },
      error: err => {
        this.error = err.error?.error || 'Logo upload failed.';
        this.uploading = false;
        this.preview = '';
      }
    });
  }

  resetLogo(): void {
    this.uploading = true;
    this.api.delete<any>('/settings/logo').subscribe({
      next: d => {
        this.apply(d);
        this.uploading = false;
        this.saved = true;
        this.preview = '';
      },
      error: err => {
        this.error = err.error?.error || 'Could not restore the default logo.';
        this.uploading = false;
      }
    });
  }

  private apply(d: any): void {
    this.data = d;
    this.fields = d.fields || [];
    this.branding.apply(d);
    this.auth.patchUser({
      app_name: d.app_name,
      app_short: d.app_short,
      organization: d.organization
    });
  }
}
