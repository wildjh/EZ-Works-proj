import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { Deuda, Evidencia, MetodoPago, MiDeudaResumen, TipoMetodoPago } from '../../core/models/api.models';
import { assetUrl } from '../../core/utils/asset-url';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe],
  templateUrl: './perfil.component.html',
})
export class PerfilComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  readonly saving = signal(false);
  readonly uploadingPhoto = signal(false);
  readonly uploadingEvidence = signal(false);
  readonly message = signal('');
  readonly error = signal('');
  readonly evidencias = signal<Evidencia[]>([]);
  readonly fotoPreview = signal<string | null>(null);

  readonly deuda = signal<MiDeudaResumen | null>(null);
  readonly metodosPago = signal<MetodoPago[]>([]);
  readonly loadingDeuda = signal(true);
  readonly pagandoDeudaId = signal<number | null>(null);
  readonly guardandoMetodo = signal(false);

  readonly metodoForm = this.fb.nonNullable.group({
    tipo: ['NEQUI' as TipoMetodoPago],
    alias: [''],
    ultimosCuatro: [''],
    predeterminado: [true],
  });

  readonly form = this.fb.nonNullable.group({
    nombre: [''],
    apellido: [''],
    telefono: [''],
    bio: [''],
  });

  ngOnInit(): void {
    const u = this.auth.currentUser();
    if (u) {
      this.patchForm(u);
      this.fotoPreview.set(assetUrl(u.fotoPerfilUrl));
    } else {
      this.api.getMe().subscribe((user) => {
        this.auth.currentUser.set(user);
        this.patchForm(user);
        this.fotoPreview.set(assetUrl(user.fotoPerfilUrl));
      });
    }

    if (this.auth.hasRole('AYUDANTE')) {
      this.loadEvidencias();
    }

    this.loadDeuda();
    this.loadMetodosPago();
  }

  private patchForm(u: {
    nombre: string;
    apellido: string;
    telefono?: string;
    perfilAyudante?: { bio?: string };
  }): void {
    this.form.patchValue({
      nombre: u.nombre,
      apellido: u.apellido,
      telefono: u.telefono ?? '',
      bio: u.perfilAyudante?.bio ?? '',
    });
  }

  loadDeuda(): void {
    this.loadingDeuda.set(true);
    this.api.getMiDeuda().subscribe({
      next: (res) => {
        this.deuda.set(res);
        this.loadingDeuda.set(false);
      },
      error: () => this.loadingDeuda.set(false),
    });
  }

  loadMetodosPago(): void {
    this.api.getMetodosPago().subscribe({
      next: (items) => this.metodosPago.set(items),
    });
  }

  onFotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadingPhoto.set(true);
    this.error.set('');
    this.api.uploadFotoPerfil(file).subscribe({
      next: (u) => {
        this.auth.currentUser.set(u);
        this.fotoPreview.set(assetUrl(u.fotoPerfilUrl));
        this.uploadingPhoto.set(false);
        this.message.set('Foto de perfil actualizada');
      },
      error: (err) => {
        this.uploadingPhoto.set(false);
        this.error.set(err?.error?.detail ?? 'No se pudo subir la foto');
      },
    });
    input.value = '';
  }

  loadEvidencias(): void {
    this.api.getMisEvidencias().subscribe({
      next: (items) => this.evidencias.set(items),
    });
  }

  onEvidenciaSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadingEvidence.set(true);
    this.error.set('');
    this.api.subirEvidencia(file).subscribe({
      next: () => {
        this.uploadingEvidence.set(false);
        this.message.set('Evidencia subida');
        this.loadEvidencias();
      },
      error: (err) => {
        this.uploadingEvidence.set(false);
        this.error.set(err?.error?.detail ?? 'No se pudo subir la evidencia');
      },
    });
    input.value = '';
  }

  eliminarEvidencia(id: number): void {
    this.api.eliminarEvidencia(id).subscribe({
      next: () => this.loadEvidencias(),
      error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo eliminar'),
    });
  }

  evidenciaUrl(path: string): string {
    return assetUrl(path) ?? '';
  }

  submit(): void {
    this.saving.set(true);
    this.message.set('');
    this.error.set('');
    this.api.updateMe(this.form.getRawValue()).subscribe({
      next: (u) => {
        this.auth.currentUser.set(u);
        this.saving.set(false);
        this.message.set('Perfil actualizado');
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.detail ?? 'Error al guardar');
      },
    });
  }

  registrarMetodoPago(): void {
    const raw = this.metodoForm.getRawValue();
    if (!raw.alias.trim()) {
      this.error.set('Indica un alias para el método de pago');
      return;
    }

    this.guardandoMetodo.set(true);
    this.api.registrarMetodoPago(raw).subscribe({
      next: () => {
        this.guardandoMetodo.set(false);
        this.message.set('Método de pago registrado');
        this.metodoForm.patchValue({ alias: '', ultimosCuatro: '' });
        this.loadMetodosPago();
      },
      error: (err) => {
        this.guardandoMetodo.set(false);
        this.error.set(err?.error?.detail ?? 'No se pudo registrar el método');
      },
    });
  }

  pagarDeuda(deudaItem: Deuda): void {
    const metodos = this.metodosPago();
    const metodo = metodos.find((m) => m.predeterminado) ?? metodos[0];
    if (!metodo) {
      this.error.set('Registra un método de pago antes de pagar la deuda');
      return;
    }

    if (!confirm(`¿Pagar ${deudaItem.monto} COP con ${metodo.alias}?`)) return;

    this.pagandoDeudaId.set(deudaItem.id);
    this.api.pagarDeuda(deudaItem.id, metodo.id).subscribe({
      next: () => {
        this.pagandoDeudaId.set(null);
        this.message.set('Pago registrado correctamente');
        this.loadDeuda();
        this.api.getMe().subscribe((u) => this.auth.currentUser.set(u));
      },
      error: (err) => {
        this.pagandoDeudaId.set(null);
        this.error.set(err?.error?.detail ?? 'No se pudo procesar el pago');
      },
    });
  }

  metodoPredeterminado(): MetodoPago | undefined {
    const metodos = this.metodosPago();
    return metodos.find((m) => m.predeterminado) ?? metodos[0];
  }
}
