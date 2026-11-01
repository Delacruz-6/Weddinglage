import { Component, Input, OnDestroy, OnChanges, SimpleChanges, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

interface CountdownParts {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

@Component({
  selector: 'app-countdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './countdown.component.html',
  styleUrl: './countdown.component.scss'
})
export class CountdownComponent implements OnChanges, OnDestroy {
  @Input({ required: true }) weddingAt: string | null = null;

  readonly parts = signal<CountdownParts | null>(null);
  readonly passed = signal(false);
  readonly invalid = signal(false);

  private timer: ReturnType<typeof setInterval> | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['weddingAt']) {
      this.restart();
    }
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  private restart(): void {
    this.clearTimer();
    this.tick();
    this.timer = setInterval(() => this.tick(), 1000);
  }

  private clearTimer(): void {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }

  private tick(): void {
    if (!this.weddingAt) {
      this.invalid.set(true);
      this.parts.set(null);
      this.passed.set(false);
      return;
    }

    const target = new Date(this.weddingAt).getTime();
    if (Number.isNaN(target)) {
      this.invalid.set(true);
      this.parts.set(null);
      this.passed.set(false);
      return;
    }

    this.invalid.set(false);
    const diff = target - Date.now();
    if (diff <= 0) {
      this.passed.set(true);
      this.parts.set({ days: 0, hours: 0, minutes: 0, seconds: 0 });
      return;
    }

    this.passed.set(false);
    const totalSeconds = Math.floor(diff / 1000);
    const days = Math.floor(totalSeconds / 86400);
    const hours = Math.floor((totalSeconds % 86400) / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    this.parts.set({ days, hours, minutes, seconds });
  }
}
