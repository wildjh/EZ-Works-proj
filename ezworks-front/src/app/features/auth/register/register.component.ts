import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RolCodigo } from '../../../core/models/api.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal('');

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    nombre: ['', Validators.required],
    apellido: ['', Validators.required],
    telefono: [''],
    rolEmpleador: [false],
    rolAyudante: [false],
    aceptaTerminos: [false, Validators.requiredTrue],
  });

  submit(): void {
    const v = this.form.getRawValue();
    const roles: RolCodigo[] = [];
    if (v.rolEmpleador) roles.push('EMPLEADOR');
    if (v.rolAyudante) roles.push('AYUDANTE');

    if (roles.length === 0) {
      this.error.set('Selecciona al menos un rol (Empleador o Ayudante)');
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.auth
      .register({
        email: v.email,
        password: v.password,
        nombre: v.nombre,
        apellido: v.apellido,
        telefono: v.telefono || undefined,
        roles,
        aceptaTerminos: v.aceptaTerminos,
      })
      .subscribe({
        next: () => {
          this.auth.loadProfile().subscribe({
            next: () => {
              this.loading.set(false);
              this.router.navigate([this.auth.defaultHomeRoute()]);
            },
            error: () => {
              this.loading.set(false);
              this.auth.clearSession();
              this.router.navigate(['/login']);
            },
          });
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(err?.error?.detail ?? 'No se pudo registrar');
        },
      });
  }
}
