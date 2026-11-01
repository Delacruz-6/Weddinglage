import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
import { SnowComponent } from '../../shared/components/snow/snow.component';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule, RouterLink, SnowComponent],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.scss'
})
export class UploadComponent implements OnInit {
  private readonly api = inject(PublicApiService);
  private readonly route = inject(ActivatedRoute);

  readonly token = signal<string | null>(null);
  readonly uploading = signal(false);
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const t = this.route.snapshot.queryParamMap.get('t');
    this.token.set(t);
    if (!t) {
      this.error.set('Falta el token de subida. Pedid un enlace válido a los novios.');
    }
  }

  onFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    const token = this.token();
    if (!file || !token) {
      return;
    }
    this.uploading.set(true);
    this.error.set(null);
    this.message.set(null);
    this.api.uploadPhoto(file, token).subscribe({
      next: (res) => {
        this.uploading.set(false);
        this.message.set(`Foto enviada (estado: ${res.status}). Gracias.`);
        input.value = '';
      },
      error: () => {
        this.uploading.set(false);
        this.error.set('No se pudo subir la foto. Comprobad el enlace e intentadlo de nuevo.');
      }
    });
  }
}
