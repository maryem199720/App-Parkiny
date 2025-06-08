import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes'; // Adjust path if your routes are defined elsewhere
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent,appConfig).catch(err => console.error(err)); {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideAnimations() // Provides BrowserAnimationsModule services
  ]
}