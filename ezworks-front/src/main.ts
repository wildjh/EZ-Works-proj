import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

/** Requerido por sockjs-client en el navegador. */
(window as unknown as { global: typeof window }).global = window;

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
