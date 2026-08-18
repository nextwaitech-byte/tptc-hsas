import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { BrandingService } from '../core/branding.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  styles: [`
    :host { display:block; height:100vh; }
    .login-page {
      height:100vh; display:grid; grid-template-columns:1.12fr 1fr; overflow:hidden;
      font-family:'DM Sans', system-ui, sans-serif;
    }
    .login-hero {
      position:relative; color:#fff; padding:2.4rem 3rem;
      display:flex; flex-direction:column; justify-content:center; overflow:hidden;
      background:
        radial-gradient(ellipse at 80% 10%, rgba(244,208,63,.16), transparent 42%),
        radial-gradient(ellipse at 10% 90%, rgba(42,157,143,.28), transparent 46%),
        linear-gradient(155deg, #061525 0%, #0c3a62 46%, #14706c 100%);
    }
    .login-hero::before {
      content:''; position:absolute; inset:0;
      background-image: linear-gradient(rgba(255,255,255,.04) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255,255,255,.04) 1px, transparent 1px);
      background-size: 42px 42px; opacity:.45; pointer-events:none;
    }
    .orb {
      position:absolute; border-radius:50%; filter:blur(8px); pointer-events:none;
      animation: drift 12s ease-in-out infinite alternate;
    }
    .orb-a { width:280px; height:280px; right:-60px; top:-40px; background:rgba(255,255,255,.09); }
    .orb-b { width:220px; height:220px; left:-50px; bottom:10%; background:rgba(42,157,143,.25); animation-duration:16s; }
    .hero-content { position:relative; z-index:1; max-width:480px; }
    .brand { display:flex; align-items:center; gap:.85rem; margin-bottom:1.4rem; }
    .brand img { width:58px; height:58px; filter: drop-shadow(0 8px 16px rgba(0,0,0,.28)); }
    .brand-name { font-family:Outfit,sans-serif; font-weight:800; letter-spacing:.04em; }
    .brand-name span { display:block; font-size:.68rem; font-weight:500; opacity:.75; letter-spacing:.12em; }
    .hero-title { font-family:Outfit,sans-serif; font-size:clamp(1.9rem,2.7vw,2.55rem); font-weight:800; line-height:1.12; margin:0 0 .75rem; letter-spacing:-.03em; }
    .hero-features { list-style:none; padding:0; margin:1.2rem 0 0; display:grid; grid-template-columns:1fr 1fr; gap:.6rem; }
    .hero-features li {
      display:flex; gap:.55rem; align-items:center; font-size:.78rem; font-weight:500;
      background:rgba(255,255,255,.08); border:1px solid rgba(255,255,255,.14);
      border-radius:14px; padding:.6rem .75rem; backdrop-filter:blur(8px);
    }
    .login-panel {
      display:flex; align-items:center; justify-content:center; padding:1.5rem;
      background:
        radial-gradient(ellipse 60% 40% at 100% 0%, rgba(26,107,154,.10), transparent 55%),
        linear-gradient(180deg, #eef3f8, #f7f9fc);
    }
    .login-box {
      width:100%; max-width:420px; border-radius:24px; padding:1.5rem 1.5rem 1.2rem;
      background:rgba(255,255,255,.78); border:1px solid rgba(255,255,255,.95);
      box-shadow: 0 1px 0 #fff inset, 0 24px 60px rgba(7,21,37,.10);
      backdrop-filter: blur(16px);
    }
    .btn-login {
      background:linear-gradient(135deg,#0d3b66,#1a6b9a); border:none; border-radius:12px;
      padding:.7rem; font-weight:700; color:#fff; width:100%; letter-spacing:.02em;
      box-shadow:0 10px 22px rgba(13,59,102,.28);
    }
    .btn-login:hover { filter:brightness(1.06); }
    .user-card {
      display:flex; align-items:center; gap:.65rem; padding:.55rem .65rem; background:#fff;
      border:1px solid #d7e0ea; border-radius:14px; width:100%; text-align:left; margin-bottom:.42rem;
      transition: transform .18s, box-shadow .18s, border-color .18s;
    }
    .user-card:hover { border-color:#1a6b9a; transform:translateX(4px); box-shadow:0 8px 20px rgba(26,107,154,.12); }
    .user-avatar { width:36px; height:36px; border-radius:11px; display:grid; place-items:center; color:#fff; background:linear-gradient(135deg,#0d3b66,#2a9d8f); flex-shrink:0; }
    @keyframes drift { to { transform: translate(-16px, 10px); } }
    @media (max-width:992px){ .login-page{grid-template-columns:1fr; height:auto;} .hero-features{grid-template-columns:1fr;} }
  `],
  template: `
<div class="login-page">
  <div class="login-hero">
    <div class="orb orb-a"></div>
    <div class="orb orb-b"></div>
    <div class="hero-content">
      <div class="brand">
        <img [src]="branding.logoSrc()" [alt]="b().organization">
        <div class="brand-name">{{ b().organization }}<span>{{ b().partners }}</span></div>
      </div>
      <h1 class="hero-title">{{ b().app_name }}</h1>
      <p style="opacity:.9;max-width:40ch;line-height:1.55">{{ b().tagline }}</p>
      <ul class="hero-features">
        <li><i class="bi bi-radar"></i> COP HQ picture</li>
        <li><i class="bi bi-grid-3x3-gap"></i> 16 data modules</li>
        <li><i class="bi bi-cpu"></i> AI decision support</li>
        <li><i class="bi bi-geo-alt"></i> Western borders</li>
      </ul>
      <div class="mt-4" style="opacity:.7;font-size:.72rem;letter-spacing:.06em">{{ b().support_line }}</div>
    </div>
  </div>
  <div class="login-panel">
    <div class="login-box">
      <div class="d-flex align-items-center gap-2 mb-2">
        <img [src]="branding.logoSrc()" alt="" width="32" height="32">
        <h2 class="mb-0" style="font-family:Outfit,sans-serif;font-weight:800;color:#0d3b66;font-size:1.32rem">Welcome back</h2>
      </div>
      <p class="text-muted" style="font-size:.8rem">{{ b().login_subtitle }}</p>
      @if (error) { <div class="alert alert-danger py-2">{{ error }}</div> }
      <form (ngSubmit)="submit()">
        <label class="form-label small fw-semibold">Email</label>
        <input class="form-control mb-2" type="email" [(ngModel)]="email" name="email" required placeholder="you&#64;tptc.go.tz">
        <label class="form-label small fw-semibold">Password</label>
        <input class="form-control mb-3" type="password" [(ngModel)]="password" name="password" required placeholder="Enter your password">
        <button class="btn-login" type="submit" [disabled]="loading">{{ loading ? 'Signing in...' : 'Sign In' }}</button>
      </form>
      <div class="text-center text-uppercase mt-3 mb-2" style="font-size:.64rem;letter-spacing:.1em;color:#94a3b8">Quick Access</div>
      @for (u of demoUsers; track u.email) {
        <button type="button" class="user-card" (click)="quick(u.email)">
          <div class="user-avatar"><i class="bi" [class]="iconFor(u.role_name)"></i></div>
          <div class="flex-grow-1">
            <div class="fw-bold" style="font-size:.78rem">{{ u.name }}</div>
            <div class="text-muted" style="font-size:.66rem">{{ u.email }}</div>
          </div>
          <span class="badge text-bg-light">{{ u.role_label }}</span>
        </button>
      }
      <p class="text-center text-muted mt-2 mb-0" style="font-size:.68rem"><i class="bi bi-info-circle"></i> Click any account to sign in (password Admin&#64;123)</p>
    </div>
  </div>
</div>
`
})
export class LoginComponent {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly branding = inject(BrandingService);
  b = this.branding.branding;
  email = '';
  password = '';
  loading = false;
  error = '';
  demoUsers: any[] = [];

  constructor() {
    if (this.auth.isLoggedIn()) this.router.navigateByUrl('/dashboard');
    this.api.get<any[]>('/auth/demo-users').subscribe(u => this.demoUsers = u);
  }

  iconFor(role: string): string {
    if (role === 'super_admin') return 'bi-star-fill';
    if (role === 'security_officer') return 'bi-shield-fill';
    return 'bi-person-badge';
  }

  quick(email: string): void {
    this.email = email;
    this.password = 'Admin@123';
    this.submit();
  }

  submit(): void {
    this.loading = true;
    this.error = '';
    this.auth.login(this.email, this.password).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: err => {
        this.error = err.error?.error || 'Invalid email or password.';
        this.loading = false;
      }
    });
  }
}
