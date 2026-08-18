import { Routes } from '@angular/router';
import { authGuard, settingsGuard } from './core/auth.guard';
import { LoginComponent } from './pages/login.component';
import { ShellComponent } from './layout/shell.component';
import { DashboardComponent } from './pages/dashboard.component';
import { ModulesComponent } from './pages/modules.component';
import { CopComponent } from './pages/cop.component';
import { AiComponent } from './pages/ai.component';
import { GisComponent } from './pages/gis.component';
import { RecordsComponent } from './pages/records.component';
import { FormComponent } from './pages/form.component';
import { ReportsComponent } from './pages/reports.component';
import { SettingsComponent } from './pages/settings.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'modules', component: ModulesComponent },
      { path: 'modules/:key', component: ModulesComponent },
      { path: 'cop', component: CopComponent },
      { path: 'ai', component: AiComponent },
      { path: 'gis', component: GisComponent },
      { path: 'early-warning', component: RecordsComponent, data: { key: 'early-warning' } },
      { path: 'intelligence', component: RecordsComponent, data: { key: 'intelligence' } },
      { path: 'intelligence/create', component: FormComponent, data: { key: 'intelligence' } },
      { path: 'intelligence/:id', component: RecordsComponent, data: { key: 'intelligence' } },
      { path: 'movements', component: RecordsComponent, data: { key: 'movements' } },
      { path: 'movements/create', component: FormComponent, data: { key: 'movements' } },
      { path: 'vehicles', component: RecordsComponent, data: { key: 'vehicles' } },
      { path: 'vehicles/create', component: FormComponent, data: { key: 'vehicles' } },
      { path: 'incidents', component: RecordsComponent, data: { key: 'incidents' } },
      { path: 'incidents/create', component: FormComponent, data: { key: 'incidents' } },
      { path: 'alerts', component: RecordsComponent, data: { key: 'alerts' } },
      { path: 'community', component: RecordsComponent, data: { key: 'community' } },
      { path: 'community/create', component: FormComponent, data: { key: 'community' } },
      { path: 'capacity', component: RecordsComponent, data: { key: 'capacity' } },
      { path: 'capacity/create', component: FormComponent, data: { key: 'capacity' } },
      { path: 'communications', component: RecordsComponent, data: { key: 'communications' } },
      { path: 'communications/create', component: FormComponent, data: { key: 'communications' } },
      { path: 'users', component: RecordsComponent, data: { key: 'users' } },
      { path: 'users/create', component: FormComponent, data: { key: 'users' } },
      { path: 'settings', component: SettingsComponent, canActivate: [settingsGuard] },
      { path: 'reports', component: ReportsComponent }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
