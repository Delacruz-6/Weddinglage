import { Component, EventEmitter, Input, Output, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-welcome-overlay',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './welcome-overlay.component.html',
  styleUrl: './welcome-overlay.component.scss'
})
export class WelcomeOverlayComponent {
  /** Kept for API compatibility; not shown on the seal screen. */
  @Input() message: string | null = null;
  @Input() partnerNames: string | null = null;
  @Input() weddingDate = '18 · 12 · 2027';

  /** Se emite en el mismo gesto del usuario (permite autoplay de audio). */
  @Output() entered = new EventEmitter<void>();
  /** Se emite cuando termina el fundido final: ahí ya puedes quitar el overlay del DOM. */
  @Output() closed = new EventEmitter<void>();

  readonly opening = signal(false);

  readonly initials = computed(() => {
    const raw = (this.partnerNames || '').trim();
    if (!raw) {
      return { left: 'G', right: 'N' };
    }
    const parts = raw
      .split(/\s*&\s*|\s+y\s+/i)
      .map((p) => p.trim())
      .filter(Boolean);
    const left = parts[0]?.charAt(0)?.toUpperCase() || 'G';
    const right = parts[1]?.charAt(0)?.toUpperCase() || 'N';
    return { left, right };
  });

  /** Hojas del laurel del sello (una rama; la otra es el espejo en el SVG). */
  readonly leaves = Array.from({ length: 7 }, (_, i) => {
    const deg = 112 + i * 17;
    const rad = (deg * Math.PI) / 180;
    const r = 33;
    return {
      x: +(50 + r * Math.cos(rad)).toFixed(2),
      y: +(50 + r * Math.sin(rad)).toFixed(2),
      rot: +(deg + 25).toFixed(1)
    };
  });

  readonly snowflakes = Array.from({ length: 36 }, (_, i) => ({
    id: i,
    left: `${(i * 17 + 7) % 100}%`,
    delay: `${-((i * 0.37) % 8)}s`,
    duration: `${7 + (i % 6)}s`,
    size: `${0.28 + (i % 5) * 0.12}rem`,
    drift: `${((i % 7) - 3) * 12}px`
  }));

  openSeal(): void {
    if (this.opening()) {
      return;
    }
    this.opening.set(true);
    // Same user-gesture turn → browser allows audio autoplay.
    this.entered.emit();
  }

  onAnimationEnd(event: AnimationEvent): void {
    if (event.target === event.currentTarget && event.animationName.includes('fade-out')) {
      this.closed.emit();
    }
  }
}
