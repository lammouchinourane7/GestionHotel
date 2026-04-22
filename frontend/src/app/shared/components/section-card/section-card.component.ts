import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-section-card',
  standalone: true,
  templateUrl: './section-card.component.html'
})
export class SectionCardComponent {
  @Input({ required: true }) title!: string;
  @Input() description = '';
  @Input() badge = '';
}
