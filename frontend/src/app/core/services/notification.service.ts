import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastNotification {
  id: number;
  type: ToastType;
  title: string;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly items = signal<ToastNotification[]>([]);
  readonly toasts = this.items.asReadonly();

  success(title: string, message?: string): void {
    this.show('success', title, message);
  }

  error(title: string, message?: string): void {
    this.show('error', title, message);
  }

  info(title: string, message?: string): void {
    this.show('info', title, message);
  }

  dismiss(id: number): void {
    this.items.update((toasts) => toasts.filter((toast) => toast.id !== id));
  }

  private show(type: ToastType, title: string, message?: string): void {
    const id = Date.now() + Math.floor(Math.random() * 1000);
    this.items.update((toasts) => [...toasts, { id, type, title, message }]);

    window.setTimeout(() => this.dismiss(id), 3800);
  }
}
