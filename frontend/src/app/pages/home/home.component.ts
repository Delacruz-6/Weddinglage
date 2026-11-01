import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
import { PublicWedding, TimelineEvent, Venue } from '../../core/models/api.models';
import { CountdownComponent } from '../../shared/components/countdown/countdown.component';
import { WelcomeOverlayComponent } from '../../shared/components/welcome-overlay/welcome-overlay.component';
import { MusicPlayerComponent } from '../../shared/components/music-player/music-player.component';
import { ChristmasMotifsComponent } from '../../shared/components/christmas-motifs/christmas-motifs.component';

const WELCOME_KEY = 'weddinglage-welcome-seen';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    CountdownComponent,
    WelcomeOverlayComponent,
    MusicPlayerComponent,
    ChristmasMotifsComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  private readonly api = inject(PublicApiService);

  @ViewChild(MusicPlayerComponent) player?: MusicPlayerComponent;

  readonly wedding = signal<PublicWedding | null>(null);
  readonly venue = signal<Venue | null>(null);
  readonly timeline = signal<TimelineEvent[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly showWelcome = signal(sessionStorage.getItem(WELCOME_KEY) !== '1');
  readonly landingReveal = signal(sessionStorage.getItem(WELCOME_KEY) === '1');
  readonly ibanCopied = signal(false);

  private ibanCopiedTimer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.api.getWedding().subscribe({
      next: (w) => {
        this.wedding.set(w);
        this.loading.set(false);
        if (w.sections?.venue) {
          this.api.getVenue().subscribe({ next: (v) => this.venue.set(v) });
        }
        if (w.sections?.timeline) {
          this.api.getTimeline().subscribe({ next: (t) => this.timeline.set(t) });
        }
        // Returning visitors: try to resume; browsers may still require a tap on ▶.
        if (!this.showWelcome()) {
          queueMicrotask(() => this.player?.startFromGesture());
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No pudimos cargar la invitación. Intentadlo de nuevo más tarde.');
      }
    });
  }

  onEnterWelcome(): void {
    sessionStorage.setItem(WELCOME_KEY, '1');
    this.landingReveal.set(true);
    // Start audio in the same gesture; overlay removes itself via (closed).
    this.player?.startFromGesture();
  }

  onWelcomeClosed(): void {
    this.showWelcome.set(false);
  }

  partnerNames(): string {
    return this.wedding()?.partnerNames?.trim() || 'Nombres pendientes';
  }

  /** Compact date for the wax-seal letter (e.g. 18 · 12 · 2027). */
  sealDate(): string {
    const at = this.wedding()?.weddingAt;
    if (!at) {
      return '18 · 12 · 2027';
    }
    try {
      const parts = new Intl.DateTimeFormat('es-ES', {
        timeZone: 'Europe/Madrid',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      }).formatToParts(new Date(at));
      const day = parts.find((p) => p.type === 'day')?.value ?? '18';
      const month = parts.find((p) => p.type === 'month')?.value ?? '12';
      const year = parts.find((p) => p.type === 'year')?.value ?? '2027';
      return `${day} · ${month} · ${year}`;
    } catch {
      return '18 · 12 · 2027';
    }
  }

  formattedDate(): string {
    const at = this.wedding()?.weddingAt;
    if (!at) {
      return 'Fecha por confirmar';
    }
    try {
      return new Intl.DateTimeFormat('es-ES', {
        timeZone: 'Europe/Madrid',
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      }).format(new Date(at));
    } catch {
      return 'Fecha por confirmar';
    }
  }

  /** Opens Google Maps (app on mobile, web on desktop) with the venue. */
  mapsUrl(v: Venue): string | null {
    const label = [v.name, v.addressLine, v.postalCode, v.city]
      .filter(Boolean)
      .join(', ');
    if (v.latitude != null && v.longitude != null && label) {
      return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${label} @${v.latitude},${v.longitude}`)}`;
    }
    if (v.latitude != null && v.longitude != null) {
      return `https://www.google.com/maps/search/?api=1&query=${v.latitude},${v.longitude}`;
    }
    return label
      ? `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(label)}`
      : null;
  }

  async copyIban(iban: string): Promise<void> {
    const value = iban.trim();
    if (!value) {
      return;
    }
    try {
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(value);
      } else {
        const ta = document.createElement('textarea');
        ta.value = value;
        ta.setAttribute('readonly', '');
        ta.style.position = 'fixed';
        ta.style.opacity = '0';
        document.body.appendChild(ta);
        ta.select();
        document.execCommand('copy');
        document.body.removeChild(ta);
      }
      this.ibanCopied.set(true);
      if (this.ibanCopiedTimer) {
        clearTimeout(this.ibanCopiedTimer);
      }
      this.ibanCopiedTimer = setTimeout(() => this.ibanCopied.set(false), 2000);
    } catch {
      this.ibanCopied.set(false);
    }
  }

  formatTime(eventTime: string | null): string {
    if (!eventTime) {
      return '';
    }
    return eventTime.length >= 5 ? eventTime.slice(0, 5) : eventTime;
  }
}
