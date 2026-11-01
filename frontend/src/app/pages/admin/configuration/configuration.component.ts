import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { WeddingConfiguration } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-configuration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './configuration.component.html',
  styleUrl: './configuration.component.scss'
})
export class ConfigurationComponent implements OnInit {
  private readonly api = inject(AdminApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    partnerNames: [''],
    weddingAt: [''],
    welcomeMessage: [''],
    storyHtml: [''],
    giftIban: [''],
    giftHolder: [''],
    showStory: [true],
    showVenue: [true],
    showTimeline: [true],
    showPlaylist: [true],
    showGifts: [true],
    showRsvp: [true],
    showPhotos: [true],
    showOrnaments: [true]
  });

  ngOnInit(): void {
    this.api.getConfiguration().subscribe({
      next: (cfg) => {
        this.patch(cfg);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No se pudo cargar la configuración.');
      }
    });
  }

  save(): void {
    const v = this.form.getRawValue();
    this.saving.set(true);
    this.message.set(null);
    this.error.set(null);

    const body: WeddingConfiguration = {
      partnerNames: v.partnerNames.trim() || null,
      weddingAt: v.weddingAt ? new Date(v.weddingAt).toISOString() : null,
      welcomeMessage: v.welcomeMessage.trim() || null,
      storyHtml: v.storyHtml.trim() || null,
      giftIban: v.giftIban.trim() || null,
      giftHolder: v.giftHolder.trim() || null,
      showStory: v.showStory,
      showVenue: v.showVenue,
      showTimeline: v.showTimeline,
      showPlaylist: v.showPlaylist,
      showGifts: v.showGifts,
      showRsvp: v.showRsvp,
      showPhotos: v.showPhotos,
      showOrnaments: v.showOrnaments
    };

    this.api.updateConfiguration(body).subscribe({
      next: (cfg) => {
        this.patch(cfg);
        this.saving.set(false);
        this.message.set('Guardado.');
      },
      error: () => {
        this.saving.set(false);
        this.error.set('No se pudo guardar.');
      }
    });
  }

  private patch(cfg: WeddingConfiguration): void {
    this.form.patchValue({
      partnerNames: cfg.partnerNames ?? '',
      weddingAt: this.toLocalInput(cfg.weddingAt),
      welcomeMessage: cfg.welcomeMessage ?? '',
      storyHtml: cfg.storyHtml ?? '',
      giftIban: cfg.giftIban ?? '',
      giftHolder: cfg.giftHolder ?? '',
      showStory: cfg.showStory,
      showVenue: cfg.showVenue,
      showTimeline: cfg.showTimeline,
      showPlaylist: cfg.showPlaylist,
      showGifts: cfg.showGifts,
      showRsvp: cfg.showRsvp,
      showPhotos: cfg.showPhotos,
      showOrnaments: cfg.showOrnaments
    });
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
