import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';

@Component({
  selector: 'app-fotos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './fotos.component.html',
  styleUrl: './fotos.component.scss'
})
export class FotosComponent implements OnInit {
  private readonly api = inject(PublicApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  readonly token = signal<string | null>(null);
  readonly uploading = signal(false);
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly lastPreview = signal<string | null>(null);
  readonly sentCount = signal(0);

  ngOnInit(): void {
    this.title.setTitle('Compartir fotos · Weddinglage');
    this.meta.updateTag({ name: 'apple-mobile-web-app-capable', content: 'yes' });
    this.meta.updateTag({ name: 'apple-mobile-web-app-status-bar-style', content: 'black-translucent' });
    this.meta.updateTag({ name: 'theme-color', content: '#0b1c16' });

    const t = this.route.snapshot.queryParamMap.get('t');
    this.token.set(t);
    if (!t) {
      this.error.set('Este enlace no es válido. Pedid el QR o el enlace de fotos a los novios.');
    }
  }

  onFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    const token = this.token();
    if (!file || !token) {
      return;
    }
    if (this.lastPreview()) {
      URL.revokeObjectURL(this.lastPreview()!);
    }
    this.lastPreview.set(URL.createObjectURL(file));
    this.uploading.set(true);
    this.error.set(null);
    this.message.set(null);
    this.api.uploadPhoto(file, token).subscribe({
      next: () => {
        this.uploading.set(false);
        this.sentCount.update((n) => n + 1);
        this.message.set('Foto enviada. Se publicará cuando la revisemos.');
        input.value = '';
      },
      error: () => {
        this.uploading.set(false);
        this.error.set('No se pudo subir. Comprobad la conexión y el enlace del QR.');
      }
    });
  }
}
