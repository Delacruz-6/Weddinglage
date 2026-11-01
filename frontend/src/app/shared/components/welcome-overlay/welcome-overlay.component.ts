import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-welcome-overlay',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './welcome-overlay.component.html',
  styleUrl: './welcome-overlay.component.scss'
})
export class WelcomeOverlayComponent {
  @Input() message: string | null = null;
  @Input() partnerNames: string | null = null;
  @Output() entered = new EventEmitter<void>();

  enter(): void {
    this.entered.emit();
  }
}
