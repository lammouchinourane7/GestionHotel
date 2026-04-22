import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Room } from '../../../models/room.model';
import { RoomApiService } from '../../../services/room-api.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

type AvailabilityFilter = 'all' | 'available' | 'unavailable';

@Component({
  selector: 'app-room-list-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    EmptyStateComponent,
    PageHeaderComponent,
    SectionCardComponent,
    StatusBadgeComponent
  ],
  templateUrl: './room-list-page.component.html'
})
export class RoomListPageComponent {
  private readonly roomApi = inject(RoomApiService);
  private readonly notificationService = inject(NotificationService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(NonNullableFormBuilder);

  readonly filterForm = this.fb.group({
    type: this.fb.control(''),
    availability: this.fb.control<AvailabilityFilter>('all')
  });

  rooms: Room[] = [];
  errorMessage = '';

  ngOnInit(): void {
    this.loadRooms();
  }

  loadRooms(): void {
    this.errorMessage = '';
    this.roomApi
      .getAllRooms()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (rooms) => {
          this.rooms = rooms;
        },
        error: (error) => {
          this.errorMessage = this.errorMessageService.resolve(
            error,
            'Impossible de charger les chambres.'
          );
          this.notificationService.error('Chargement impossible', this.errorMessage);
        }
      });
  }

  applyFilters(): void {
    const { type, availability } = this.filterForm.getRawValue();
    const normalizedType = type.trim();

    const applyAvailability = (rooms: Room[]): Room[] => {
      if (availability === 'available') {
        return rooms.filter((room) => room.available);
      }

      if (availability === 'unavailable') {
        return rooms.filter((room) => !room.available);
      }

      return rooms;
    };

    const request$ = normalizedType
      ? this.roomApi.searchByType(normalizedType)
      : availability !== 'all'
        ? this.roomApi.searchByAvailability(availability === 'available')
        : this.roomApi.getAllRooms();

    request$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (rooms) => {
        this.rooms = normalizedType ? applyAvailability(rooms) : rooms;
      },
      error: (error) => {
        this.rooms = [];
        this.notificationService.info(
          'Aucun resultat',
          this.errorMessageService.resolve(error, 'Aucune chambre ne correspond au filtre courant.')
        );
      }
    });
  }

  resetFilters(): void {
    this.filterForm.setValue({ type: '', availability: 'all' });
    this.loadRooms();
  }

  deleteRoom(room: Room): void {
    if (!room.id) {
      return;
    }

    const confirmed = window.confirm(
      `Supprimer la chambre ${room.number} ? Cette action est irreversible.`
    );

    if (!confirmed) {
      return;
    }

    this.roomApi
      .deleteRoom(room.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.success('Chambre supprimee', 'La chambre a ete retiree avec succes.');
          this.loadRooms();
        },
        error: (error) => {
          this.notificationService.error(
            'Suppression impossible',
            this.errorMessageService.resolve(error, 'La chambre n a pas pu etre supprimee.')
          );
        }
      });
  }
}
