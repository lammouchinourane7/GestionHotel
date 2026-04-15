import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './empty-state.component.html'
})
export class EmptyStateComponent {
  @Input({ required: true }) title!: string;
  @Input() description = '';
  @Input() actionLabel = '';
  @Input() actionLink = '';
}
