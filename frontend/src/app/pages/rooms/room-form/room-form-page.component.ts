import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Room } from '../../../models/room.model';
import { RoomApiService } from '../../../services/room-api.service';
import { FormFieldErrorComponent } from '../../../shared/components/form-field-error/form-field-error.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';

@Component({
  selector: 'app-room-form-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    FormFieldErrorComponent,
    PageHeaderComponent,
    SectionCardComponent
  ],
  templateUrl: './room-form-page.component.html'
})
export class RoomFormPageComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly roomApi = inject(RoomApiService);
  private readonly notificationService = inject(NotificationService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly roomId = Number(this.route.snapshot.paramMap.get('id')) || null;
  readonly isEditMode = !!this.roomId;
  readonly roomTypes = ['STANDARD', 'DELUXE', 'SUITE', 'FAMILY'];

  readonly form = this.fb.group({
    number: ['', Validators.required],
    type: ['', Validators.required],
    capacity: [1, [Validators.required, Validators.min(1)]],
    pricePerNight: [1, [Validators.required, Validators.min(1)]],
    available: [true]
  });

  submitError = '';

  ngOnInit(): void {
    if (this.roomId) {
      this.roomApi
        .getRoomById(this.roomId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (room) => this.form.patchValue(room),
          error: (error) => {
            this.submitError = this.errorMessageService.resolve(
              error,
              'Impossible de charger la chambre demandee.'
            );
            this.notificationService.error('Chargement impossible', this.submitError);
          }
        });
    }
  }

  submit(): void {
    this.submitError = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: Room = this.form.getRawValue();
    const request$ = this.isEditMode && this.roomId
      ? this.roomApi.updateRoom(this.roomId, payload)
      : this.roomApi.createRoom(payload);

    request$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (room) => {
        this.notificationService.success(
          this.isEditMode ? 'Chambre modifiee' : 'Chambre ajoutee',
          `La chambre ${room.number} est disponible dans l inventaire.`
        );
        this.router.navigate(['/rooms']);
      },
      error: (error) => {
        this.submitError = this.errorMessageService.resolve(
          error,
          'Impossible d enregistrer la chambre.'
        );
        this.notificationService.error('Enregistrement impossible', this.submitError);
      }
    });
  }
}
