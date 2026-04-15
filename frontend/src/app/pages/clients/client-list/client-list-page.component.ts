import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ErrorMessageService } from '../../../core/services/error-message.service';
import { NotificationService } from '../../../core/services/notification.service';
import {
  Client,
  ClientPageResponse,
  ClientPageSearchMode,
  ClientStatistics
} from '../../../models/client.model';
import { ClientApiService } from '../../../services/client-api.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../../shared/components/section-card/section-card.component';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';

@Component({
  selector: 'app-client-list-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    EmptyStateComponent,
    PageHeaderComponent,
    SectionCardComponent,
    StatCardComponent
  ],
  templateUrl: './client-list-page.component.html'
})
export class ClientListPageComponent {
  private readonly clientApi = inject(ClientApiService);
  private readonly notificationService = inject(NotificationService);
  private readonly errorMessageService = inject(ErrorMessageService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(NonNullableFormBuilder);

  readonly searchForm = this.fb.group({
    mode: this.fb.control<ClientPageSearchMode>('ALL'),
    query: this.fb.control(''),
    sortBy: this.fb.control('createdAt'),
    direction: this.fb.control('desc'),
    size: this.fb.control(6)
  });

  clientsPage: ClientPageResponse | null = null;
  statistics: ClientStatistics | null = null;
  errorMessage = '';
  isExporting = false;

  ngOnInit(): void {
    this.loadClients();
    this.loadStatistics();
  }

  get clients(): Client[] {
    return this.clientsPage?.content ?? [];
  }

  loadClients(page = 0): void {
    this.errorMessage = '';
    const { mode, query, sortBy, direction, size } = this.searchForm.getRawValue();

    this.clientApi
      .getClientsPage({ page, size, sortBy, direction, mode, query: query.trim() })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (clientsPage) => {
          this.clientsPage = clientsPage;
        },
        error: (error) => {
          this.errorMessage = this.errorMessageService.resolve(
            error,
            'Impossible de charger la liste paginee des clients.'
          );
          this.notificationService.error('Chargement impossible', this.errorMessage);
        }
      });
  }

  loadStatistics(): void {
    this.clientApi
      .getClientStatistics()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (statistics) => {
          this.statistics = statistics;
        },
        error: (error) => {
          this.notificationService.info(
            'Statistiques indisponibles',
            this.errorMessageService.resolve(error, 'Les statistiques clients ne sont pas disponibles pour le moment.')
          );
        }
      });
  }

  searchClients(): void {
    this.loadClients(0);
  }

  resetSearch(): void {
    this.searchForm.setValue({
      mode: 'ALL',
      query: '',
      sortBy: 'createdAt',
      direction: 'desc',
      size: 6
    });
    this.loadClients(0);
  }

  previousPage(): void {
    if (!this.clientsPage || this.clientsPage.page === 0) {
      return;
    }

    this.loadClients(this.clientsPage.page - 1);
  }

  nextPage(): void {
    if (!this.clientsPage || this.clientsPage.page >= this.clientsPage.totalPages - 1) {
      return;
    }

    this.loadClients(this.clientsPage.page + 1);
  }

  exportCsv(): void {
    this.isExporting = true;

    this.clientApi
      .downloadClientsCsv()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob);
          const anchor = document.createElement('a');
          anchor.href = url;
          anchor.download = 'clients-export.csv';
          anchor.click();
          URL.revokeObjectURL(url);
          this.isExporting = false;
          this.notificationService.success('Export pret', 'Le fichier CSV des clients a ete telecharge.');
        },
        error: (error) => {
          this.isExporting = false;
          this.notificationService.error(
            'Export impossible',
            this.errorMessageService.resolve(error, 'Le CSV clients n a pas pu etre genere.')
          );
        }
      });
  }

  deleteClient(client: Client): void {
    if (!client.id) {
      return;
    }

    const confirmed = window.confirm(
      `Supprimer le client ${client.firstName} ${client.lastName} ? Cette action publiera aussi un evenement RabbitMQ.`
    );

    if (!confirmed) {
      return;
    }

    this.clientApi
      .deleteClient(client.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.success(
            'Client supprime',
            'Le client a ete retire et les reservations liees pourront etre annulees cote backend.'
          );
          this.loadClients(this.clientsPage?.page ?? 0);
          this.loadStatistics();
        },
        error: (error) => {
          this.notificationService.error(
            'Suppression impossible',
            this.errorMessageService.resolve(error, 'Le client n a pas pu etre supprime.')
          );
        }
      });
  }
}
