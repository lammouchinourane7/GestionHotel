import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  templateUrl: './status-badge.component.html'
})
export class StatusBadgeComponent {
  @Input({ required: true }) label!: string;
  @Input() variant: 'success' | 'warning' | 'danger' | 'info' | 'neutral' = 'neutral';

  get classes(): string {
    switch (this.variant) {
      case 'success':
        return 'bg-teal-50 text-teal-700 ring-teal-200';
      case 'warning':
        return 'bg-amber-50 text-amber-700 ring-amber-200';
      case 'danger':
        return 'bg-red-50 text-red-700 ring-red-200';
      case 'info':
        return 'bg-sky-50 text-sky-700 ring-sky-200';
      default:
        return 'bg-slate-100 text-slate-600 ring-slate-200';
    }
  }
}
