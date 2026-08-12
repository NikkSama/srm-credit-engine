import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';

export class AppConfig {
  public static config: ApplicationConfig = {
    providers: [
      provideHttpClient()
    ]
  };
}