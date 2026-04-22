import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin, of } from 'rxjs';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';
import { Reservation, ReservationClientOption, ReservationRoomOption } from '../../../models/reservation.model';
import { ReservationApiService } from '../../../services/reservation-api.service';
import { RoomApiService } from '../../../services/room-api.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { FormFieldErrorComponent } from '../../../shared/components/form-field-error/form-field-error.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { dateRangeValidator } from '../../../shared/validators/date-range.validator';

@Component({
  selector: 'app-reservation-form-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    EmptyStateComponent,
    FormFieldErrorComponent,
    PageHeaderComponent,
    SectionCardComponent,
    StatusBadgeComponent
  ],
  templateUrl: './reservation-form-page.component.html'
})
export class ReservationFormPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly roomApi = inject(RoomApiService);
  private readonly reservationApi = inject(ReservationApiService);
  private readonly notificationService = inject(NotificationService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly authService = inject(AuthService);

  readonly form = this.fb.group(
    {
      clientId: [0, [Validators.required, Validators.min(1)]],
      roomId: [0, [Validators.required, Validators.min(1)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    },
    { validators: dateRangeValidator('startDate', 'endDate') }
  );

  clients: ReservationClientOption[] = [];
  rooms: ReservationRoomOption[] = [];
  selectedRoomPrice: number | null = null;
  selectedRoomAvailable: boolean | null = null;
  submitError = '';

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  ngOnInit(): void {
    this.loadOptions();
    this.watchRoomSelection();

    if (!this.isAdmin) {
      this.form.controls.clientId.setValue(1);
      this.form.controls.clientId.disable();
    }
  }

  get nightsCount(): number {
    const startDate = this.form.controls.startDate.value;
    const endDate = this.form.controls.endDate.value;

    if (!startDate || !endDate) {
      return 0;
    }

    const diff = new Date(endDate).getTime() - new Date(startDate).getTime();
    return diff > 0 ? Math.round(diff / (1000 * 60 * 60 * 24)) : 0;
  }

  get estimatedTotal(): number | null {
    if (!this.selectedRoomPrice || !this.nightsCount) {
      return null;
    }

    return this.selectedRoomPrice * this.nightsCount;
  }

  loadOptions(): void {
    const clientsRequest$ = this.isAdmin
      ? this.reservationApi.getReservationClientOptions()
      : of([] as ReservationClientOption[]);

    forkJoin({
      clients: clientsRequest$,
      rooms: this.reservationApi.getReservationRoomOptions()
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ({ clients, rooms }) => {
          this.clients = clients;
          this.rooms = rooms;
        },
        error: (error) => {
          this.submitError = this.errorMessageService.resolve(
            error,
            'Impossible de charger les clients et les chambres.'
          );
        }
      });
  }

  watchRoomSelection(): void {
    this.form.controls.roomId.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((roomId) => {
      if (!roomId || roomId < 1) {
        this.selectedRoomPrice = null;
        this.selectedRoomAvailable = null;
        return;
      }

      forkJoin({
        price: this.roomApi.getRoomPrice(roomId),
        availability: this.roomApi.getRoomAvailability(roomId)
      })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: ({ price, availability }) => {
            this.selectedRoomPrice = price.pricePerNight;
            this.selectedRoomAvailable = availability.available;
          },
          error: () => {
            this.selectedRoomPrice = null;
            this.selectedRoomAvailable = null;
          }
        });
    });
  }

  submit(): void {
    this.submitError = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.getRawValue() as Reservation;

    this.reservationApi
      .createReservation(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (reservation) => {
          this.notificationService.success(
            'Reservation creee',
            `La reservation #${reservation.id} a ete creee avec le statut ${reservation.status}.`
          );
          this.router.navigate(['/reservations', reservation.id]);
        },
        error: (error) => {
          this.submitError = this.errorMessageService.resolve(
            error,
            'La reservation n a pas pu etre creee.'
          );
          this.notificationService.error('Creation impossible', this.submitError);
        }
      });
  }
}
