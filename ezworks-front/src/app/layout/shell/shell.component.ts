import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserAvatarComponent } from '../../core/components/user-avatar/user-avatar.component';
import { assetUrl } from '../../core/utils/asset-url';
import { RolCodigo } from '../../core/models/api.models';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, UserAvatarComponent],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css',
})
export class ShellComponent implements OnInit {
  readonly auth = inject(AuthService);
  readonly menuOpen = signal(false);

  readonly themeRole = computed(() => {
    const roles = this.auth.roles();
    if (roles.includes('ADMIN')) return 'admin';
    if (roles.includes('EMPLEADOR')) return 'empleador';
    if (roles.includes('AYUDANTE')) return 'ayudante';
    return 'default';
  });

  readonly brandLabel = computed(() => {
    switch (this.themeRole()) {
      case 'admin':
        return { icon: '🛡️', text: 'EZWorks Admin' };
      case 'empleador':
        return { icon: '💼', text: 'EZWorks Empleador' };
      case 'ayudante':
        return { icon: '🔧', text: 'EZWorks Ayudante' };
      default:
        return { icon: '⚡', text: 'EZWorks' };
    }
  });

  ngOnInit(): void {
    if (this.auth.getToken() && !this.auth.sessionReady()) {
      this.auth.ensureSession().subscribe();
    }
  }

  toggleMenu(): void {
    this.menuOpen.update((v) => !v);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }

  logout(): void {
    this.auth.logout();
  }

  fotoUsuario(): string | null {
    const u = this.auth.currentUser();
    return u ? assetUrl(u.fotoPerfilUrl) : null;
  }

  nombreUsuario(): string {
    const u = this.auth.currentUser();
    return u ? `${u.nombre} ${u.apellido}` : '';
  }

  hasRole(role: RolCodigo): boolean {
    return this.auth.hasRole(role);
  }
}
