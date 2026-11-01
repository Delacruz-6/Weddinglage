import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
@Component({
  selector: 'app-ornament',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './ornament.component.html',
  styleUrl: './ornament.component.scss'
})
export class OrnamentComponent implements OnInit {
  private readonly api = inject(PublicApiService);

  readonly suggestion = signal<string | null>(null);

  ngOnInit(): void {
    this.api.getWedding().subscribe({
      next: (w) => this.suggestion.set(w.ornamentSuggestion)
    });
  }
}
