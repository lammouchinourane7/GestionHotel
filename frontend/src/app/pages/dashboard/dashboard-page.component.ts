import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ErrorMessageService } from '../../core/services/error-message.service';
import { DashboardOverview } from '../../models/dashboard.model';
import { DashboardService } from '../../services/dashboard.service';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { SectionCardComponent } from '../../shared/components/section-card/section-card.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-dashboard-page',
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
  templateUrl: './dashboard-page.component.html'
})
export class DashboardPageComponent {
  private readonly dashboardService = inject(DashboardService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly errorMessageService = inject(ErrorMessageService);

  overview: DashboardOverview | null = null;
  errorMessage = '';

  ngOnInit(): void {
    this.loadOverview();
  }

  loadOverview(): void {
    this.errorMessage = '';

    this.dashboardService
      .getDashboardOverview()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (overview) => {
          this.overview = overview;
        },
        error: (error) => {
          this.errorMessage = this.errorMessageService.resolve(
            error,
            'Impossible de charger les statistiques du dashboard.'
          );
        }
      });
  }

  get occupancyRate(): number {
    if (!this.overview || this.overview.stats.totalRooms === 0) {
      return 0;
    }

    const occupied = this.overview.stats.totalRooms - this.overview.stats.availableRooms;
    return Math.round((occupied / this.overview.stats.totalRooms) * 100);
  }
}
