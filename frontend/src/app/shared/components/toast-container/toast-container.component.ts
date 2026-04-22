import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';

import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.component.html'
})
export class ToastContainerComponent {
  readonly notificationService = inject(NotificationService);

  getToastClasses(type: string): string {
    switch (type) {
      case 'success':
        return 'border-teal-200 bg-teal-50 text-teal-900';
      case 'error':
        return 'border-red-200 bg-red-50 text-red-900';
      default:
        return 'border-sky-200 bg-sky-50 text-sky-900';
    }
  }
}
