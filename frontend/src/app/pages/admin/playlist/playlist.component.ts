import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { PlaylistTrack } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-playlist',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './playlist.component.html',
  styleUrl: './playlist.component.scss'
})
export class PlaylistComponent implements OnInit {
  private readonly api = inject(AdminApiService);

  readonly items = signal<PlaylistTrack[]>([]);
  readonly error = signal<string | null>(null);
  readonly message = signal<string | null>(null);

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.listPlaylist().subscribe({
      next: (list) => this.items.set(list),
      error: () => this.error.set('No se pudo cargar la playlist.')
    });
  }

  remove(item: PlaylistTrack): void {
    if (item.id == null || !confirm(`¿Quitar «${item.title}»?`)) {
      return;
    }
    this.api.deleteTrack(item.id).subscribe({
      next: () => {
        this.message.set('Canción eliminada.');
        this.reload();
      },
      error: () => this.error.set('No se pudo eliminar.')
    });
  }
}
