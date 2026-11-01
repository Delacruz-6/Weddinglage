import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
import { PublicWedding, TimelineEvent, Venue } from '../../core/models/api.models';
import { CountdownComponent } from '../../shared/components/countdown/countdown.component';
import { WelcomeOverlayComponent } from '../../shared/components/welcome-overlay/welcome-overlay.component';
import { MusicPlayerComponent } from '../../shared/components/music-player/music-player.component';
import { SnowComponent } from '../../shared/components/snow/snow.component';

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
    SnowComponent
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
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No pudimos cargar la invitación. Intentadlo de nuevo más tarde.');
      }
    });
  }

  onEnterWelcome(): void {
    sessionStorage.setItem(WELCOME_KEY, '1');
    this.showWelcome.set(false);
    this.player?.startFromGesture();
  }

  partnerNames(): string {
    return this.wedding()?.partnerNames?.trim() || 'Nombres pendientes';
  }

  formattedDate(): string {
    const at = this.wedding()?.weddingAt;
    if (!at) {
      return 'Fecha por confirmar';
    }
    try {
      return new Intl.DateTimeFormat('es-ES', {
        timeZone: 'Europe/Madrid',
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(new Date(at));
    } catch {
      return 'Fecha por confirmar';
    }
  }

  googleMapsUrl(v: Venue): string | null {
    if (v.latitude != null && v.longitude != null) {
      return `https://www.google.com/maps/search/?api=1&query=${v.latitude},${v.longitude}`;
    }
    const q = [v.addressLine, v.city, v.postalCode, v.country].filter(Boolean).join(', ');
    return q ? `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(q)}` : null;
  }

  appleMapsUrl(v: Venue): string | null {
    if (v.latitude != null && v.longitude != null) {
      return `https://maps.apple.com/?ll=${v.latitude},${v.longitude}&q=${encodeURIComponent(v.name || 'Lugar')}`;
    }
    const q = [v.addressLine, v.city, v.postalCode, v.country].filter(Boolean).join(', ');
    return q ? `https://maps.apple.com/?q=${encodeURIComponent(q)}` : null;
  }

  formatTime(eventTime: string | null): string {
    if (!eventTime) {
      return '';
    }
    return eventTime.length >= 5 ? eventTime.slice(0, 5) : eventTime;
  }
}
