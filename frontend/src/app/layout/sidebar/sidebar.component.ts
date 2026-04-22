import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface NavItem {
  label: string;
  route: string;
  exact?: boolean;
  description: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html'
})
export class SidebarComponent {
  @Input() isOpen = false;
  @Output() closeSidebar = new EventEmitter<void>();

  readonly navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', exact: true, description: 'Vue d ensemble' },
    { label: 'Clients', route: '/clients', description: 'Gestion des clients' },
    { label: 'Rooms', route: '/rooms', description: 'Gestion des chambres' },
    { label: 'Reservations', route: '/reservations', description: 'Cycle de reservation' }
  ];
}
