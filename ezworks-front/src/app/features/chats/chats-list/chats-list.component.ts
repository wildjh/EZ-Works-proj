import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ConversacionResumen } from '../../../core/models/api.models';
import { UserAvatarComponent } from '../../../core/components/user-avatar/user-avatar.component';
import { assetUrl } from '../../../core/utils/asset-url';

@Component({
  selector: 'app-chats-list',
  standalone: true,
  imports: [RouterLink, DatePipe, UserAvatarComponent],
  templateUrl: './chats-list.component.html',
})
export class ChatsListComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);

  readonly chats = signal<ConversacionResumen[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    this.api.getMisConversaciones().subscribe({
      next: (items) => {
        this.chats.set(items);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'No se pudieron cargar los chats');
        this.loading.set(false);
      },
    });
  }

  abrirChat(id: number, event: Event): void {
    event.preventDefault();
    this.router.navigate(['/chat', id]);
  }

  fotoChat(c: ConversacionResumen): string | null {
    return assetUrl(c.otroParticipanteFotoUrl);
  }

  nombreChat(c: ConversacionResumen): string {
    return `${c.otroParticipanteNombre} ${c.otroParticipanteApellido}`;
  }
}
