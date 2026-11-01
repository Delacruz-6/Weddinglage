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
    heroImageUrl: [''],
    storyImageUrl: [''],
    spotifyPlaylistUrl: [''],
    ornamentSuggestion: [''],
    showStory: [true],
    showVenue: [true],
    showTimeline: [true],
    showPlaylist: [true],
    showGifts: [true],
    showRsvp: [true],
    showPhotos: [true],
    showOrnaments: [true],
    rsvpShowNotes: [true],
    rsvpShowDietary: [true],
    rsvpShowTransport: [true],
    rsvpShowSongs: [true],
    rsvpShowChildren: [true]
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

    let weddingAt: string | null = null;
    if (v.weddingAt.trim()) {
      const parsed = new Date(v.weddingAt);
      if (Number.isNaN(parsed.getTime())) {
        this.saving.set(false);
        this.error.set('La fecha y hora no son válidas.');
        return;
      }
      weddingAt = parsed.toISOString();
    }

    const body: WeddingConfiguration = {
      partnerNames: v.partnerNames.trim() || null,
      weddingAt,
      welcomeMessage: v.welcomeMessage.trim() || null,
      storyHtml: v.storyHtml.trim() || null,
      giftIban: v.giftIban.trim() || null,
      giftHolder: v.giftHolder.trim() || null,
      heroImageUrl: v.heroImageUrl.trim() || null,
      storyImageUrl: v.storyImageUrl.trim() || null,
      spotifyPlaylistUrl: v.spotifyPlaylistUrl.trim() || null,
      ornamentSuggestion: v.ornamentSuggestion.trim() || null,
      showStory: v.showStory,
      showVenue: v.showVenue,
      showTimeline: v.showTimeline,
      showPlaylist: v.showPlaylist,
      showGifts: v.showGifts,
      showRsvp: v.showRsvp,
      showPhotos: v.showPhotos,
      showOrnaments: v.showOrnaments,
      rsvpShowNotes: v.rsvpShowNotes,
      rsvpShowDietary: v.rsvpShowDietary,
      rsvpShowTransport: v.rsvpShowTransport,
      rsvpShowSongs: v.rsvpShowSongs,
      rsvpShowChildren: v.rsvpShowChildren
    };

    this.api.updateConfiguration(body).subscribe({
      next: (cfg) => {
        this.patch(cfg);
        this.saving.set(false);
        this.message.set('Guardado.');
      },
      error: (err) => {
        this.saving.set(false);
        const status = err?.status as number | undefined;
        const msg = err?.error?.message as string | undefined;
        if (status === 401 || status === 403) {
          this.error.set('Sesión caducada. Volvé a iniciar sesión en /admin/login.');
          return;
        }
        this.error.set(msg || 'No se pudo guardar la configuración.');
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
      heroImageUrl: cfg.heroImageUrl ?? '',
      storyImageUrl: cfg.storyImageUrl ?? '',
      spotifyPlaylistUrl: cfg.spotifyPlaylistUrl ?? '',
      ornamentSuggestion: cfg.ornamentSuggestion ?? '',
      showStory: cfg.showStory,
      showVenue: cfg.showVenue,
      showTimeline: cfg.showTimeline,
      showPlaylist: cfg.showPlaylist,
      showGifts: cfg.showGifts,
      showRsvp: cfg.showRsvp,
      showPhotos: cfg.showPhotos,
      showOrnaments: cfg.showOrnaments,
      rsvpShowNotes: cfg.rsvpShowNotes,
      rsvpShowDietary: cfg.rsvpShowDietary,
      rsvpShowTransport: cfg.rsvpShowTransport,
      rsvpShowSongs: cfg.rsvpShowSongs,
      rsvpShowChildren: cfg.rsvpShowChildren
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
