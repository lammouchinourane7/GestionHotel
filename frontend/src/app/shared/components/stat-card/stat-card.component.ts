import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  templateUrl: './stat-card.component.html'
})
export class StatCardComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) value!: number | string;
  @Input() description = '';
  @Input() accent: 'teal' | 'amber' | 'rose' | 'slate' = 'teal';

  get accentClasses(): string {
    switch (this.accent) {
      case 'amber':
        return 'from-amber-50 to-white text-amber-700';
      case 'rose':
        return 'from-rose-50 to-white text-rose-700';
      case 'slate':
        return 'from-slate-100 to-white text-slate-700';
      default:
        return 'from-teal-50 to-white text-teal-700';
    }
  }
}
