import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-exports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './exports.component.html',
  styleUrl: './exports.component.scss'
})
export class ExportsComponent {
  private readonly api = inject(AdminApiService);

  readonly links = [
    { kind: 'guests' as const, label: 'Invitados (CSV)' },
    { kind: 'catering' as const, label: 'Catering (CSV)' },
    { kind: 'transport' as const, label: 'Transporte (CSV)' },
    { kind: 'songs' as const, label: 'Canciones (CSV)' },
    { kind: 'ornaments' as const, label: 'Adornos (CSV)' }
  ];

  url(kind: 'guests' | 'catering' | 'transport' | 'songs' | 'ornaments'): string {
    return this.api.exportUrl(kind);
  }
}
