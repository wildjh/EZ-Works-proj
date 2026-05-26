import { CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Requerimiento } from '../../../core/models/api.models';

@Component({
  selector: 'app-mis-requerimientos',
  standalone: true,
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './mis-requerimientos.component.html',
  styleUrl: './mis-requerimientos.component.css',
})
export class MisRequerimientosComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly items = signal<Requerimiento[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    this.api.getMisRequerimientos().subscribe({
      next: (list) => {
        this.items.set(list);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'Error al cargar');
        this.loading.set(false);
      },
    });
  }

  estadoLabel(estado: string): string {
    const map: Record<string, string> = {
      BORRADOR: 'Borrador',
      PUBLICADO: 'Publicado',
      EN_MATCH: 'Con match',
      FINALIZADO: 'Finalizado',
      CANCELADO: 'Cancelado',
    };
    return map[estado] ?? estado;
  }
}
