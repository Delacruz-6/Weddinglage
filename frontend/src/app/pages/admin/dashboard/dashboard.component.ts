import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { AuthService } from '../../../core/services/auth.service';
import { Dashboard } from '../../../core/models/api.models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(AdminApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly data = signal<Dashboard | null>(null);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.getDashboard().subscribe({
      next: (d) => this.data.set(d),
      error: () => {
        this.auth.clearSession();
        this.error.set('No se pudo cargar el panel. Iniciad sesión de nuevo.');
        void this.router.navigateByUrl('/admin/login');
      }
    });
  }
}
