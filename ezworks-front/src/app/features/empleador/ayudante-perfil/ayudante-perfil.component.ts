import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { PerfilAyudantePublico } from '../../../core/models/api.models';
import { UserAvatarComponent } from '../../../core/components/user-avatar/user-avatar.component';
import { assetUrl } from '../../../core/utils/asset-url';

@Component({
  selector: 'app-ayudante-perfil',
  standalone: true,
  imports: [RouterLink, UserAvatarComponent],
  templateUrl: './ayudante-perfil.component.html',
  styleUrl: './ayudante-perfil.component.css',
})
export class AyudantePerfilComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);

  readonly perfil = signal<PerfilAyudantePublico | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly chatId = signal<number | null>(null);

  ngOnInit(): void {
    const chatParam = this.route.snapshot.queryParamMap.get('chat');
    if (chatParam) {
      const id = Number(chatParam);
      if (!Number.isNaN(id) && id > 0) {
        this.chatId.set(id);
      }
    }

    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.api.getPerfilAyudantePublico(id).subscribe({
      next: (p) => {
        this.perfil.set(p);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'No se pudo cargar el perfil');
        this.loading.set(false);
      },
    });
  }

  fotoUrl(): string | null {
    const p = this.perfil();
    return p ? assetUrl(p.fotoPerfilUrl) : null;
  }

  nombreCompleto(): string {
    const p = this.perfil();
    return p ? `${p.nombre} ${p.apellido}` : '';
  }

  evidenciaUrl(path: string): string {
    return assetUrl(path) ?? '';
  }
}
