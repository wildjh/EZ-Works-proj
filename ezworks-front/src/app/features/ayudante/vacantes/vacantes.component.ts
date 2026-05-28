import { CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Requerimiento } from '../../../core/models/api.models';

@Component({
  selector: 'app-vacantes',
  standalone: true,
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './vacantes.component.html',
})
export class VacantesComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly items = signal<Requerimiento[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    this.api.getVacantes().subscribe({
      next: (list) => {
        this.items.set(list);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'Error al cargar vacantes');
        this.loading.set(false);
      },
    });
  }
}
