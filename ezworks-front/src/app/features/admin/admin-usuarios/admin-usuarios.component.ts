import { Component, inject, OnInit, signal } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';
import { AdminUsuario, EstadoCuenta } from '../../../core/models/api.models';
import { UserAvatarComponent } from '../../../core/components/user-avatar/user-avatar.component';
import { assetUrl } from '../../../core/utils/asset-url';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [UserAvatarComponent],
  templateUrl: './admin-usuarios.component.html',
})
export class AdminUsuariosComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly usuarios = signal<AdminUsuario[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly actionMsg = signal('');

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.api.getAdminUsuarios().subscribe({
      next: (items) => {
        this.usuarios.set(items);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'No se pudieron cargar usuarios');
        this.loading.set(false);
      },
    });
  }

  fotoUrl(u: AdminUsuario): string | null {
    return assetUrl(u.fotoPerfilUrl);
  }

  nombreCompleto(u: AdminUsuario): string {
    return `${u.nombre} ${u.apellido}`;
  }

  cambiarEstado(u: AdminUsuario, estado: EstadoCuenta): void {
    this.actionMsg.set('');
    this.error.set('');
    this.api.actualizarEstadoUsuario(u.id, estado).subscribe({
      next: () => {
        this.actionMsg.set(`Estado de ${u.email} actualizado`);
        this.load();
      },
      error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo actualizar'),
    });
  }
}
