import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Reservation } from '../../../models/reservation.model';
import { ReservationApiService } from '../../../services/reservation-api.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

type ReservationStatusFilter = 'all' | 'CREATED' | 'CONFIRMED' | 'CANCELLED';

@Component({
  selector: 'app-reservation-list-page',
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
  templateUrl: './reservation-list-page.component.html'
})
export class ReservationListPageComponent {
  private readonly reservationApi = inject(ReservationApiService);
  private readonly notificationService = inject(NotificationService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(NonNullableFormBuilder);

  readonly filterForm = this.fb.group({
    status: this.fb.control<ReservationStatusFilter>('all'),
    clientId: this.fb.control(''),
    roomId: this.fb.control('')
  });

  reservations: Reservation[] = [];
  errorMessage = '';

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.errorMessage = '';
    this.reservationApi
      .getAllReservations()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (reservations) => {
          this.reservations = reservations;
        },
        error: (error) => {
          this.errorMessage = this.errorMessageService.resolve(
            error,
            'Impossible de charger les reservations.'
          );
          this.notificationService.error('Chargement impossible', this.errorMessage);
        }
      });
  }

  applyFilters(): void {
    const status = this.filterForm.controls.status.value;
    const clientId = Number(this.filterForm.controls.clientId.value);
    const roomId = Number(this.filterForm.controls.roomId.value);

    const request$ = clientId
      ? this.reservationApi.getReservationsByClientId(clientId)
      : roomId
        ? this.reservationApi.getReservationsByRoomId(roomId)
        : this.reservationApi.getAllReservations();

    request$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (reservations) => {
        let filtered = reservations;

        if (clientId && roomId) {
          filtered = filtered.filter((reservation) => reservation.roomId === roomId);
        }

        if (status !== 'all') {
          filtered = filtered.filter((reservation) => reservation.status === status);
        }

        this.reservations = filtered;
      },
      error: (error) => {
        this.reservations = [];
        this.notificationService.info(
          'Aucun resultat',
          this.errorMessageService.resolve(error, 'Aucune reservation ne correspond aux filtres selectionnes.')
        );
      }
    });
  }

  resetFilters(): void {
    this.filterForm.setValue({ status: 'all', clientId: '', roomId: '' });
    this.loadReservations();
  }

  confirmReservation(reservation: Reservation): void {
    if (!reservation.id) {
      return;
    }

    this.reservationApi
      .confirmReservation(reservation.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.success('Reservation confirmee', `La reservation #${reservation.id} est confirmee.`);
          this.loadReservations();
        },
        error: (error) => {
          this.notificationService.error(
            'Confirmation impossible',
            this.errorMessageService.resolve(error, 'La reservation n a pas pu etre confirmee.')
          );
        }
      });
  }

  cancelReservation(reservation: Reservation): void {
    if (!reservation.id) {
      return;
    }

    this.reservationApi
      .cancelReservation(reservation.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.success('Reservation annulee', `La reservation #${reservation.id} a ete annulee.`);
          this.loadReservations();
        },
        error: (error) => {
          this.notificationService.error(
            'Annulation impossible',
            this.errorMessageService.resolve(error, 'La reservation n a pas pu etre annulee.')
          );
        }
      });
  }

  deleteReservation(reservation: Reservation): void {
    if (!reservation.id) {
      return;
    }

    const confirmed = window.confirm(`Supprimer definitivement la reservation #${reservation.id} ?`);
    if (!confirmed) {
      return;
    }

    this.reservationApi
      .deleteReservation(reservation.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.success('Reservation supprimee', 'La reservation a ete retiree.');
          this.loadReservations();
        },
        error: (error) => {
          this.notificationService.error(
            'Suppression impossible',
            this.errorMessageService.resolve(error, 'La reservation n a pas pu etre supprimee.')
          );
        }
      });
  }

  getStatusVariant(status?: string): 'success' | 'warning' | 'danger' | 'neutral' {
    if (status === 'CONFIRMED') {
      return 'success';
    }

    if (status === 'CREATED') {
      return 'warning';
    }

    if (status === 'CANCELLED') {
      return 'danger';
    }

    return 'neutral';
  }
}
