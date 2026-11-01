import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicApiService } from '../../../core/services/public-api.service';
import { PlaylistTrack } from '../../../core/models/api.models';

const MUTE_KEY = 'weddinglage-music-muted';
const VOLUME_KEY = 'weddinglage-music-volume';

@Component({
  selector: 'app-music-player',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './music-player.component.html',
  styleUrl: './music-player.component.scss'
})
export class MusicPlayerComponent implements OnInit, OnDestroy {
  private readonly api = inject(PublicApiService);

  readonly tracks = signal<PlaylistTrack[]>([]);
  readonly index = signal(0);
  readonly playing = signal(false);
  readonly muted = signal(localStorage.getItem(MUTE_KEY) === '1');
  readonly volume = signal(Number(localStorage.getItem(VOLUME_KEY) ?? '0.6'));
  readonly error = signal<string | null>(null);

  private audio: HTMLAudioElement | null = null;
  private startedByGesture = false;

  ngOnInit(): void {
    this.api.getPlaylist().subscribe({
      next: (list) => this.tracks.set(list ?? []),
      error: () => this.error.set('No se pudo cargar la lista')
    });
  }

  ngOnDestroy(): void {
    this.stopAudio();
  }

  /** Call from a user gesture (welcome Entrar / play). */
  startFromGesture(): void {
    this.startedByGesture = true;
    if (!this.playing()) {
      this.play();
    }
  }

  togglePlay(): void {
    if (!this.startedByGesture) {
      this.startedByGesture = true;
    }
    if (this.playing()) {
      this.pause();
    } else {
      this.play();
    }
  }

  toggleMute(): void {
    const next = !this.muted();
    this.muted.set(next);
    localStorage.setItem(MUTE_KEY, next ? '1' : '0');
    if (this.audio) {
      this.audio.muted = next;
    }
  }

  onVolume(event: Event): void {
    const value = Number((event.target as HTMLInputElement).value);
    this.volume.set(value);
    localStorage.setItem(VOLUME_KEY, String(value));
    if (this.audio) {
      this.audio.volume = value;
    }
  }

  next(): void {
    const list = this.tracks();
    if (!list.length) {
      return;
    }
    this.index.set((this.index() + 1) % list.length);
    if (this.playing() || this.startedByGesture) {
      this.play();
    }
  }

  prev(): void {
    const list = this.tracks();
    if (!list.length) {
      return;
    }
    this.index.set((this.index() - 1 + list.length) % list.length);
    if (this.playing() || this.startedByGesture) {
      this.play();
    }
  }

  current(): PlaylistTrack | null {
    return this.tracks()[this.index()] ?? null;
  }

  private play(): void {
    const track = this.current();
    if (!track?.sourceUrl) {
      return;
    }
    if (!this.audio || this.audio.src !== track.sourceUrl) {
      this.stopAudio();
      this.audio = new Audio(track.sourceUrl);
      this.audio.preload = 'metadata';
      this.audio.addEventListener('ended', () => this.next());
    }
    this.audio.muted = this.muted();
    this.audio.volume = this.volume();
    void this.audio.play().then(
      () => this.playing.set(true),
      () => {
        this.playing.set(false);
        this.error.set('No se pudo reproducir');
      }
    );
  }

  private pause(): void {
    this.audio?.pause();
    this.playing.set(false);
  }

  private stopAudio(): void {
    if (this.audio) {
      this.audio.pause();
      this.audio.src = '';
      this.audio = null;
    }
    this.playing.set(false);
  }
}
