import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const dateRangeValidator = (
  startControlName: string,
  endControlName: string
): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    const startValue = control.get(startControlName)?.value;
    const endValue = control.get(endControlName)?.value;

    if (!startValue || !endValue) {
      return null;
    }

    const startDate = new Date(startValue);
    const endDate = new Date(endValue);

    return startDate < endDate ? null : { dateRange: true };
  };
};
