import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
import { OrnamentPublic } from '../../core/models/api.models';
import { SnowComponent } from '../../shared/components/snow/snow.component';

@Component({
  selector: 'app-ornament',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SnowComponent],
  templateUrl: './ornament.component.html',
  styleUrl: './ornament.component.scss'
})
export class OrnamentComponent implements OnInit {
  private readonly api = inject(PublicApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly ornament = signal<OrnamentPublic | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  inviteInput = '';

  ngOnInit(): void {
    const code = this.route.snapshot.paramMap.get('inviteCode');
    if (code && code !== '_') {
      this.inviteInput = code;
      this.load(code);
    }
  }

  lookup(): void {
    const code = this.inviteInput.trim();
    if (!code) {
      return;
    }
    void this.router.navigate(['/ornament', code]);
    this.load(code);
  }

  private load(code: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getOrnament(code).subscribe({
      next: (o) => {
        this.ornament.set(o);
        this.loading.set(false);
      },
      error: () => {
        this.ornament.set(null);
        this.loading.set(false);
        this.error.set('No encontramos un adorno para ese código.');
      }
    });
  }
}
