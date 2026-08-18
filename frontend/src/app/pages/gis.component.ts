import { AfterViewInit, Component, inject } from '@angular/core';
import { ApiService } from '../core/api.service';

declare const L: any;

@Component({
  selector: 'app-gis',
  template: `
<div class="page-header"><div><h5 class="fw-bold mb-0">GIS & Spatial</h5><p>Border posts, camps and intelligence locations</p></div></div>
<div id="hsas-map" style="height:640px;border-radius:14px;overflow:hidden"></div>
`
})
export class GisComponent implements AfterViewInit {
  private readonly api = inject(ApiService);
  ngAfterViewInit(): void {
    this.api.get<any>('/gis').subscribe(data => {
      const map = L.map('hsas-map').setView([-4.3, 30.2], 6);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '© OSM' }).addTo(map);
      (data.posts || []).forEach((p: any) => {
        if (p.latitude && p.longitude) {
          L.circleMarker([p.latitude, p.longitude], { radius: 9, color: p.level === 'high' ? '#dc2626' : '#0d3b66' })
            .addTo(map).bindPopup(`<b>${p.name}</b><br>Risk ${p.risk_score}`);
        }
      });
      (data.camps || []).forEach((c: any) => {
        if (c.latitude && c.longitude) {
          L.marker([c.latitude, c.longitude]).addTo(map)
            .bindPopup(`<b>${c.name}</b><br>${c.current_population} / ${c.capacity}`);
        }
      });
    });
  }
}
