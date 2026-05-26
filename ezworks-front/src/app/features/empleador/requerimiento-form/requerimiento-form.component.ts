import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Categoria } from '../../../core/models/api.models';

@Component({
  selector: 'app-requerimiento-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './requerimiento-form.component.html',
  styleUrl: './requerimiento-form.component.css',
})
export class RequerimientoFormComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly categorias = signal<Categoria[]>([]);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly editId = signal<number | null>(null);

  readonly form = this.fb.nonNullable.group({
    categoriaId: [0, [Validators.required, Validators.min(1)]],
    titulo: ['', Validators.required],
    descripcion: ['', Validators.required],
    remuneracion: [0, [Validators.required, Validators.min(1)]],
    zonaAproximada: [''],
    direccionExacta: [''],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.editId.set(id);
      this.api.getRequerimiento(id).subscribe({
        next: (r) => {
          if (r.estado !== 'BORRADOR') {
            this.error.set('Solo se pueden editar borradores');
            return;
          }
          this.form.patchValue({
            categoriaId: r.categoriaId,
            titulo: r.titulo,
            descripcion: r.descripcion,
            remuneracion: r.remuneracion,
            zonaAproximada: r.zonaAproximada ?? '',
            direccionExacta: r.direccionExacta ?? '',
          });
        },
        error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo cargar'),
      });
    }

    this.api.getCategorias().subscribe((c) => this.categorias.set(c));
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set('');
    const v = this.form.getRawValue();
    const body = {
      categoriaId: v.categoriaId,
      titulo: v.titulo,
      descripcion: v.descripcion,
      remuneracion: v.remuneracion,
      zonaAproximada: v.zonaAproximada || undefined,
      direccionExacta: v.direccionExacta || undefined,
    };

    const editId = this.editId();
    const req$ = editId
      ? this.api.actualizarRequerimiento(editId, body)
      : this.api.crearRequerimiento(body);

    req$.subscribe({
      next: (r) => this.router.navigate(['/empleador/requerimientos', r.id]),
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.detail ?? 'Error al guardar');
      },
    });
  }
}
