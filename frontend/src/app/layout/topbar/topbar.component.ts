import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, inject } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.component.html'
})
export class TopbarComponent {
  @Output() menuToggle = new EventEmitter<void>();

  readonly now = new Date();
  readonly authService = inject(AuthService);

  async login(): Promise<void> {
    await this.authService.login();
  }

  async logout(): Promise<void> {
    await this.authService.logout();
  }
}
