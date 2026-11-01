import { Component } from '@angular/core';

@Component({
  selector: 'app-snow',
  standalone: true,
  templateUrl: './snow.component.html',
  styleUrl: './snow.component.scss'
})
export class SnowComponent {
  readonly flakes = Array.from({ length: 36 }, (_, i) => i);
}
