import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Client, ClientStayPreferences } from '../../../models/client.model';
import { ClientApiService } from '../../../services/client-api.service';
import { FormFieldErrorComponent } from '../../../shared/components/form-field-error/form-field-error.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';

const EMPTY_STAY_PREFERENCES: ClientStayPreferences = {
  preferredRoomType: '',
  preferredBedType: '',
  preferredFloor: null,
  quietRoom: false,
  nonSmoking: false,
  highFloor: false,
  needsBabyBed: false,
  specialRequests: ''
};

@Component({
  selector: 'app-client-form-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    FormFieldErrorComponent,
    PageHeaderComponent,
    SectionCardComponent
  ],
  templateUrl: './client-form-page.component.html'
})
export class ClientFormPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly clientApi = inject(ClientApiService);
  private readonly notificationService = inject(NotificationService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', Validators.required],
    address: [''],
    nationalId: [''],
    stayPreferences: this.fb.group({
      preferredRoomType: [''],
      preferredBedType: [''],
      preferredFloor: [null as number | null],
      quietRoom: [false],
      nonSmoking: [false],
      highFloor: [false],
      needsBabyBed: [false],
      specialRequests: ['']
    })
  });

  readonly clientId = Number(this.route.snapshot.paramMap.get('id')) || null;
  readonly isEditMode = !!this.clientId;
  submitError = '';

  ngOnInit(): void {
    if (!this.clientId) {
      return;
    }

    this.clientApi
      .getClientById(this.clientId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (client) => this.form.patchValue(this.normalizeClient(client)),
        error: (error) => {
          this.submitError = this.errorMessageService.resolve(
            error,
            'Impossible de charger la fiche client.'
          );
          this.notificationService.error('Chargement impossible', this.submitError);
        }
      });
  }

  submit(): void {
    this.submitError = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.normalizeClient(this.form.getRawValue() as Client);
    const request$ = this.isEditMode && this.clientId
      ? this.clientApi.updateClient(this.clientId, payload)
      : this.clientApi.createClient(payload);

    request$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (client) => {
        this.notificationService.success(
          this.isEditMode ? 'Client modifie' : 'Client cree',
          `La fiche de ${client.firstName} ${client.lastName} est maintenant disponible.`
        );
        this.router.navigate(['/clients']);
      },
      error: (error) => {
        this.submitError = this.errorMessageService.resolve(
          error,
          'L operation sur le client a echoue.'
        );
        this.notificationService.error('Enregistrement impossible', this.submitError);
      }
    });
  }

  private normalizeClient(client: Partial<Client>): Client {
    return {
      id: client.id,
      firstName: client.firstName ?? '',
      lastName: client.lastName ?? '',
      email: client.email ?? '',
      phone: client.phone ?? '',
      address: client.address ?? '',
      nationalId: client.nationalId ?? '',
      createdAt: client.createdAt,
      updatedAt: client.updatedAt,
      stayPreferences: {
        ...EMPTY_STAY_PREFERENCES,
        ...(client.stayPreferences ?? {})
      }
    };
  }
}
