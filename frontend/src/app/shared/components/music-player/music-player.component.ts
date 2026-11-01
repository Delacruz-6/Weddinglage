import {
  AfterViewInit,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  inject,
  signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

export interface LocalTrack {
  id: string;
  title: string;
  artist?: string | null;
  src: string;
}

interface LocalPlaylist {
  name: string;
  tracks: LocalTrack[];
}

@Component({
  selector: 'app-music-player',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './music-player.component.html',
  styleUrl: './music-player.component.scss'
})
export class MusicPlayerComponent implements OnChanges, AfterViewInit, OnDestroy {
  private readonly http = inject(HttpClient);

  @Input() enabled = true;

  readonly playlistName = signal('Nuestra playlist');
  readonly tracks = signal<LocalTrack[]>([]);
  readonly index = signal(0);
  readonly playing = signal(false);
  readonly positionMs = signal(0);
  readonly durationMs = signal(0);
  readonly visible = signal(false);

  private audio: HTMLAudioElement | null = null;
  private startedByGesture = false;
  private viewReady = false;
  private pendingStart = false;
  private loaded = false;
  private unlocked = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['enabled']) {
      if (this.enabled) {
        this.visible.set(true);
        this.ensurePlaylistLoaded();
      } else {
        this.visible.set(false);
        this.pauseAudio();
      }
    }
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.enabled) {
      this.visible.set(true);
      this.ensurePlaylistLoaded();
    }
    if (this.pendingStart) {
      this.pendingStart = false;
      void this.beginPlayback();
    }
  }

  ngOnDestroy(): void {
    this.teardownAudio();
  }

  /**
   * Call from the same user-gesture turn as Abrir / play.
   * Unlocks audio immediately so a later playlist HTTP response can still autoplay.
   */
  startFromGesture(): void {
    this.startedByGesture = true;
    this.visible.set(true);
    this.unlockAudio();
    if (!this.viewReady || !this.loaded) {
      this.pendingStart = true;
      this.ensurePlaylistLoaded();
      return;
    }
    void this.beginPlayback();
  }

  togglePlay(): void {
    if (!this.startedByGesture) {
      this.startFromGesture();
      return;
    }
    const audio = this.ensureAudio();
    if (this.playing()) {
      audio.pause();
      this.playing.set(false);
      return;
    }
    void audio.play().then(() => this.playing.set(true)).catch(() => this.playing.set(false));
  }

  next(): void {
    const list = this.tracks();
    if (list.length < 1) {
      return;
    }
    this.index.set((this.index() + 1) % list.length);
    if (this.startedByGesture) {
      void this.playCurrent(true);
    } else {
      this.loadCurrentSrc(false);
    }
  }

  prev(): void {
    const list = this.tracks();
    if (list.length < 1) {
      return;
    }
    this.index.set((this.index() - 1 + list.length) % list.length);
    if (this.startedByGesture) {
      void this.playCurrent(true);
    } else {
      this.loadCurrentSrc(false);
    }
  }

  onSeek(event: Event): void {
    const value = Number((event.target as HTMLInputElement).value);
    const audio = this.audio;
    if (!audio || !Number.isFinite(value)) {
      return;
    }
    audio.currentTime = value / 1000;
    this.positionMs.set(value);
  }

  progressPercent(): number {
    const d = this.durationMs();
    if (!d) {
      return 0;
    }
    return Math.min(100, Math.round((this.positionMs() / d) * 100));
  }

  displayTitle(): string {
    return this.current()?.title || this.playlistName();
  }

  formatMs(ms: number): string {
    if (!ms || ms < 0) {
      return '0:00';
    }
    const total = Math.floor(ms / 1000);
    const m = Math.floor(total / 60);
    const s = String(total % 60).padStart(2, '0');
    return `${m}:${s}`;
  }

  current(): LocalTrack | null {
    return this.tracks()[this.index()] ?? null;
  }

  private ensurePlaylistLoaded(): void {
    if (this.loaded || !this.enabled) {
      return;
    }
    this.http.get<LocalPlaylist>('/music/playlist.json').subscribe({
      next: (pl) => {
        this.playlistName.set(pl.name || 'Nuestra playlist');
        this.tracks.set(pl.tracks ?? []);
        this.index.set(0);
        this.loaded = true;
        this.visible.set(this.enabled && (pl.tracks?.length ?? 0) > 0);
        if (this.pendingStart || this.startedByGesture) {
          this.pendingStart = false;
          void this.beginPlayback();
        }
      },
      error: () => {
        // Stay quiet — never surface playlist failures in the UI.
        this.visible.set(this.enabled);
      }
    });
  }

  private async beginPlayback(): Promise<void> {
    try {
      await this.playCurrent(true);
    } catch {
      this.playing.set(false);
    }
  }

  /** Consume the user-gesture so later async play() is allowed. */
  private unlockAudio(): void {
    if (this.unlocked) {
      return;
    }
    const audio = this.ensureAudio();
    this.unlocked = true;
    const prevMuted = audio.muted;
    audio.muted = true;
    void audio
      .play()
      .then(() => {
        audio.pause();
        audio.currentTime = 0;
        audio.muted = prevMuted;
      })
      .catch(() => {
        audio.muted = prevMuted;
      });
  }

  private ensureAudio(): HTMLAudioElement {
    if (this.audio) {
      return this.audio;
    }
    const audio = new Audio();
    audio.preload = 'auto';
    audio.addEventListener('timeupdate', () => {
      this.positionMs.set(Math.floor(audio.currentTime * 1000));
    });
    audio.addEventListener('loadedmetadata', () => {
      this.durationMs.set(Math.floor((audio.duration || 0) * 1000));
    });
    audio.addEventListener('play', () => this.playing.set(true));
    audio.addEventListener('pause', () => this.playing.set(false));
    audio.addEventListener('ended', () => this.onTrackEnded());
    audio.addEventListener('error', () => this.playing.set(false));
    this.audio = audio;
    return audio;
  }

  private loadCurrentSrc(autoplay: boolean): void {
    const track = this.current();
    const audio = this.ensureAudio();
    if (!track) {
      return;
    }
    if (audio.src !== new URL(track.src, window.location.origin).href) {
      audio.src = track.src;
      audio.load();
    }
    this.positionMs.set(0);
    if (autoplay) {
      void audio.play().then(() => this.playing.set(true)).catch(() => this.playing.set(false));
    }
  }

  private async playCurrent(forcePlay: boolean): Promise<void> {
    this.loadCurrentSrc(forcePlay);
  }

  private onTrackEnded(): void {
    const list = this.tracks();
    if (list.length > 1) {
      this.index.set((this.index() + 1) % list.length);
      void this.playCurrent(true);
      return;
    }
    const audio = this.audio;
    if (audio) {
      audio.currentTime = 0;
      void audio.play().catch(() => this.playing.set(false));
    }
  }

  private pauseAudio(): void {
    this.audio?.pause();
    this.playing.set(false);
  }

  private teardownAudio(): void {
    if (this.audio) {
      this.audio.pause();
      this.audio.removeAttribute('src');
      this.audio.load();
      this.audio = null;
    }
    this.playing.set(false);
    this.positionMs.set(0);
    this.durationMs.set(0);
  }
}
