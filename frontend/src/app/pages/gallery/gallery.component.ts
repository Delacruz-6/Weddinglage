import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
import { PhotoPublic } from '../../core/models/api.models';
import { SnowComponent } from '../../shared/components/snow/snow.component';

@Component({
  selector: 'app-gallery',
  standalone: true,
  imports: [CommonModule, RouterLink, SnowComponent],
  templateUrl: './gallery.component.html',
  styleUrl: './gallery.component.scss'
})
export class GalleryComponent implements OnInit {
  private readonly api = inject(PublicApiService);

  readonly photos = signal<PhotoPublic[]>([]);
  readonly loading = signal(true);
  readonly emptyMessage = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.listPhotos().subscribe({
      next: (list) => {
        this.photos.set(list ?? []);
        this.loading.set(false);
        if (!list?.length) {
          this.emptyMessage.set(
            'La galería aún no está publicada o no hay fotos disponibles.'
          );
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No se pudo cargar la galería.');
        this.emptyMessage.set(
          'La galería aún no está publicada o no hay fotos disponibles.'
        );
      }
    });
  }

  src(id: number): string {
    return this.api.photoContentUrl(id);
  }
}
