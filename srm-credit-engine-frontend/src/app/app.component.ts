import { Component } from '@angular/core';
import { Dashboard } from './features/dashboard/dashboard';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [Dashboard],
  template: '<app-dashboard />',
})
export class AppComponent {}
