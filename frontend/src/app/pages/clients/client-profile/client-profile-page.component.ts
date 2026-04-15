import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { ClientChange, ClientHistory, ClientProfile } from '../../../models/client.model';
import { ClientApiService } from '../../../services/client-api.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-client-profile-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    EmptyStateComponent,
    PageHeaderComponent,
    SectionCardComponent,
    StatCardComponent,
    StatusBadgeComponent
  ],
  templateUrl: './client-profile-page.component.html'
})
export class ClientProfilePageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly clientApi = inject(ClientApiService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly destroyRef = inject(DestroyRef);

  readonly clientId = Number(this.route.snapshot.paramMap.get('id')) || null;

  profile: ClientProfile | null = null;
  history: ClientHistory | null = null;
  changes: ClientChange[] = [];
  errorMessage = '';

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    if (!this.clientId) {
      this.errorMessage = 'Client introuvable.';
      return;
    }

    forkJoin({
      profile: this.clientApi.getClientProfile(this.clientId),
      history: this.clientApi.getClientHistory(this.clientId),
      changes: this.clientApi.getClientChanges(this.clientId)
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ({ profile, history, changes }) => {
          this.profile = profile;
          this.history = history;
          this.changes = changes;
        },
        error: (error) => {
          this.errorMessage = this.errorMessageService.resolve(
            error,
            'Impossible de charger le profil client enrichi.'
          );
        }
      });
  }

  get loyaltyVariant(): 'success' | 'warning' | 'danger' | 'info' | 'neutral' {
    switch (this.profile?.loyalty.status) {
      case 'PLATINUM':
        return 'success';
      case 'GOLD':
        return 'info';
      case 'SILVER':
        return 'warning';
      case 'BRONZE':
        return 'neutral';
      default:
        return 'neutral';
    }
  }
}
