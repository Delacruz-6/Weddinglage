import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { PhotoAdmin, PublicationConfig, UploadTokenResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-photos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './photos.component.html',
  styleUrl: './photos.component.scss'
})
export class PhotosComponent implements OnInit {
  private readonly api = inject(AdminApiService);
  private readonly fb = inject(FormBuilder);

  readonly photos = signal<PhotoAdmin[]>([]);
  readonly publication = signal<PublicationConfig | null>(null);
  readonly createdToken = signal<UploadTokenResponse | null>(null);
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly pubForm = this.fb.nonNullable.group({
    publishAt: [''],
    adminAuthorized: [false]
  });

  readonly tokenForm = this.fb.nonNullable.group({
    label: [''],
    expiresAt: ['']
  });

  ngOnInit(): void {
    this.reloadPhotos();
    this.api.getPublication().subscribe({
      next: (p) => {
        this.publication.set(p);
        this.pubForm.patchValue({
          publishAt: this.toLocalInput(p.publishAt),
          adminAuthorized: p.adminAuthorized
        });
      },
      error: () => this.error.set('No se pudo cargar la publicación.')
    });
  }

  reloadPhotos(): void {
    this.api.listPhotos().subscribe({
      next: (list) => this.photos.set(list),
      error: () => this.error.set('No se pudieron cargar las fotos.')
    });
  }

  setStatus(id: number, status: 'APPROVED' | 'REJECTED' | 'PENDING'): void {
    this.api.patchPhoto(id, { status }).subscribe({
      next: () => this.reloadPhotos(),
      error: () => this.error.set('No se pudo actualizar el estado.')
    });
  }

  remove(id: number): void {
    if (!confirm('¿Eliminar esta foto?')) {
      return;
    }
    this.api.deletePhoto(id).subscribe({
      next: () => this.reloadPhotos(),
      error: () => this.error.set('No se pudo eliminar.')
    });
  }

  savePublication(): void {
    const v = this.pubForm.getRawValue();
    this.api
      .updatePublication({
        publishAt: v.publishAt ? new Date(v.publishAt).toISOString() : null,
        adminAuthorized: v.adminAuthorized
      })
      .subscribe({
        next: (p) => {
          this.publication.set(p);
          this.message.set('Publicación actualizada.');
        },
        error: () => this.error.set('No se pudo guardar la publicación.')
      });
  }

  createToken(): void {
    const v = this.tokenForm.getRawValue();
    this.api
      .createUploadToken({
        label: v.label.trim() || null,
        expiresAt: v.expiresAt ? new Date(v.expiresAt).toISOString() : null
      })
      .subscribe({
        next: (t) => {
          this.createdToken.set(t);
          this.message.set('Token creado.');
        },
        error: () => this.error.set('No se pudo crear el token.')
      });
  }

  uploadUrl(token: string): string {
    const origin = typeof window !== 'undefined' ? window.location.origin : '';
    return `${origin}/fotos?t=${encodeURIComponent(token)}`;
  }

  private toLocalInput(iso: string | null): string {
    if (!iso) {
      return '';
    }
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) {
      return '';
    }
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }
}
