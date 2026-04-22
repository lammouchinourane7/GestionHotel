import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';
import { ToastContainerComponent } from './shared/components/toast-container/toast-container.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, LoadingSpinnerComponent, ToastContainerComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {}
