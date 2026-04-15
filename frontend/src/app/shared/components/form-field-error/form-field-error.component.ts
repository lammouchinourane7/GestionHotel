import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-form-field-error',
  standalone: true,
  templateUrl: './form-field-error.component.html'
})
export class FormFieldErrorComponent {
  @Input() control: AbstractControl | null = null;
  @Input() label = 'This field';

  get shouldShow(): boolean {
    return !!this.control && this.control.invalid && (this.control.touched || this.control.dirty);
  }

  get message(): string {
    if (!this.control?.errors) {
      return '';
    }

    if (this.control.errors['required']) {
      return `${this.label} is required.`;
    }

    if (this.control.errors['email']) {
      return 'Please enter a valid email address.';
    }

    if (this.control.errors['min']) {
      return `${this.label} must be greater than 0.`;
    }

    return 'Please review this field.';
  }
}
