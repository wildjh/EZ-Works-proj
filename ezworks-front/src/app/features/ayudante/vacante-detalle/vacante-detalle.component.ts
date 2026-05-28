import { CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Requerimiento } from '../../../core/models/api.models';

@Component({
  selector: 'app-vacante-detalle',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, CurrencyPipe],
  templateUrl: './vacante-detalle.component.html',
})
export class VacanteDetalleComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly req = signal<Requerimiento | null>(null);
  readonly loading = signal(true);
  readonly posting = signal(false);
  readonly error = signal('');
  readonly success = signal('');

  readonly form = this.fb.nonNullable.group({
    mensajePresentacion: [''],
  });

  private id = 0;

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.api.getRequerimiento(this.id).subscribe({
      next: (r) => {
        this.req.set(r);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'No encontrado');
        this.loading.set(false);
      },
    });
  }

  postular(): void {
    this.posting.set(true);
    this.error.set('');
    this.api.postular(this.id, this.form.value.mensajePresentacion ?? '').subscribe({
      next: () => {
        this.posting.set(false);
        this.success.set('Postulación enviada correctamente');
      },
      error: (err) => {
        this.posting.set(false);
        this.error.set(err?.error?.detail ?? 'No se pudo postular');
      },
    });
  }
}
