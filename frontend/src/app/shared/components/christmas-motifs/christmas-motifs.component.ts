import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-christmas-motifs',
  standalone: true,
  templateUrl: './christmas-motifs.component.html',
  styleUrl: './christmas-motifs.component.scss',
  host: {
    // Only corners become absolute; divider must stay in normal flow.
    '[class.hero-corners]': 'variant === "corners"'
  }
})
export class ChristmasMotifsComponent {
  /** Corner flourishes for section frames. */
  @Input() variant: 'corners' | 'divider' = 'corners';
}
