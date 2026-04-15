import { Component, inject } from '@angular/core';

import { LoadingService } from '../../../core/services/loading.service';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  templateUrl: './loading-spinner.component.html'
})
export class LoadingSpinnerComponent {
  readonly loadingService = inject(LoadingService);
}
