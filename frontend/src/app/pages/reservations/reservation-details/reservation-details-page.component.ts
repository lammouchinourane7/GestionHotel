import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ReservationDetails } from '../../../models/reservation.model';
import { ReservationApiService } from '../../../services/reservation-api.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-reservation-details-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    EmptyStateComponent,
    PageHeaderComponent,
    SectionCardComponent,
    StatusBadgeComponent
  ],
  templateUrl: './reservation-details-page.component.html'
})
export class ReservationDetailsPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly reservationApi = inject(ReservationApiService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly notificationService = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);

  readonly reservationId = Number(this.route.snapshot.paramMap.get('id')) || null;

  reservation: ReservationDetails | null = null;
  errorMessage = '';

  ngOnInit(): void {
    this.loadReservation();
  }

  loadReservation(): void {
    if (!this.reservationId) {
      this.errorMessage = 'Reservation introuvable.';
      return;
    }

    this.reservationApi
      .getReservationDetails(this.reservationId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (reservation) => {
          this.reservation = reservation;
        },
        error: (error) => {
          this.errorMessage = this.errorMessageService.resolve(
            error,
            'Impossible de charger le detail de la reservation.'
          );
        }
      });
  }

  confirmReservation(): void {
    if (!this.reservationId) {
      return;
    }

    this.reservationApi
      .confirmReservation(this.reservationId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.success('Reservation confirmee', `La reservation #${this.reservationId} est confirmee.`);
          this.loadReservation();
        },
        error: (error) => {
          this.notificationService.error(
            'Confirmation impossible',
            this.errorMessageService.resolve(error, 'La reservation n a pas pu etre confirmee.')
          );
        }
      });
  }

  cancelReservation(): void {
    if (!this.reservationId) {
      return;
    }

    this.reservationApi
      .cancelReservation(this.reservationId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.success('Reservation annulee', `La reservation #${this.reservationId} a ete annulee.`);
          this.loadReservation();
        },
        error: (error) => {
          this.notificationService.error(
            'Annulation impossible',
            this.errorMessageService.resolve(error, 'La reservation n a pas pu etre annulee.')
          );
        }
      });
  }

  get statusVariant(): 'success' | 'warning' | 'danger' | 'neutral' {
    if (this.reservation?.status === 'CONFIRMED') {
      return 'success';
    }

    if (this.reservation?.status === 'CREATED') {
      return 'warning';
    }

    if (this.reservation?.status === 'CANCELLED') {
      return 'danger';
    }

    return 'neutral';
  }
}
