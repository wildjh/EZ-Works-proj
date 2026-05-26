import { DatePipe } from '@angular/common';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { ChatRealtimeService } from '../../core/services/chat-realtime.service';
import { Conversacion, Mensaje } from '../../core/models/api.models';
import { UserAvatarComponent } from '../../core/components/user-avatar/user-avatar.component';
import { assetUrl } from '../../core/utils/asset-url';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DatePipe, UserAvatarComponent],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit, OnDestroy {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly realtime = inject(ChatRealtimeService);
  readonly auth = inject(AuthService);

  readonly conv = signal<Conversacion | null>(null);
  readonly loading = signal(true);
  readonly sending = signal(false);
  readonly error = signal('');

  readonly form = this.fb.nonNullable.group({ contenido: [''] });

  private conversacionId = 0;
  private unsubscribeWs: (() => void) | null = null;

  ngOnInit(): void {
    this.conversacionId = Number(this.route.snapshot.paramMap.get('id'));
    if (!this.conversacionId || Number.isNaN(this.conversacionId)) {
      this.error.set('Conversación no válida');
      this.loading.set(false);
      return;
    }
    this.load(this.conversacionId);
    this.unsubscribeWs = this.realtime.subscribe(this.conversacionId, (msg) =>
      this.appendMessage(msg)
    );
  }

  ngOnDestroy(): void {
    this.unsubscribeWs?.();
  }

  load(id: number): void {
    this.loading.set(true);
    this.api.getConversacion(id).subscribe({
      next: (c) => {
        this.conv.set(c);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'No se pudo cargar el chat');
        this.loading.set(false);
      },
    });
  }

  enviar(): void {
    const texto = this.form.value.contenido?.trim();
    const c = this.conv();
    if (!texto || !c || !c.activa) return;

    this.sending.set(true);
    this.api.enviarMensaje(c.id, texto).subscribe({
      next: (msg) => {
        this.form.reset();
        this.sending.set(false);
        this.appendMessage(msg);
        if (!c.puedeEnviar && this.auth.hasRole('AYUDANTE')) {
          this.load(c.id);
        }
      },
      error: (err) => {
        this.sending.set(false);
        this.error.set(err?.error?.detail ?? 'Error al enviar');
      },
    });
  }

  private appendMessage(msg: Mensaje): void {
    this.conv.update((c) => {
      if (!c || c.id !== msg.conversacionId) return c;
      if (c.mensajes.some((m) => m.id === msg.id)) return c;
      const updated = { ...c, mensajes: [...c.mensajes, msg] };
      if (!c.puedeEnviar && this.auth.currentUser()?.id !== c.empleadorUsuarioId) {
        updated.puedeEnviar = true;
      }
      return updated;
    });
  }

  isMine(emisorId: number): boolean {
    return this.auth.currentUser()?.id === emisorId;
  }

  fotoInterlocutor(c: Conversacion): string | null {
    return assetUrl(c.otroParticipanteFotoUrl);
  }

  nombreInterlocutor(c: Conversacion): string {
    return `${c.otroParticipanteNombre ?? ''} ${c.otroParticipanteApellido ?? ''}`.trim();
  }

  perfilLink(c: Conversacion): string[] | null {
    if (c.otroParticipantePerfilAyudanteId) {
      return ['/empleador/ayudantes', String(c.otroParticipantePerfilAyudanteId)];
    }
    if (c.otroParticipantePerfilEmpleadorId) {
      return ['/ayudante/empleadores', String(c.otroParticipantePerfilEmpleadorId)];
    }
    return null;
  }

  perfilQueryParams(c: Conversacion): { chat: string } {
    return { chat: String(c.id) };
  }

  mensajeCierre(c: Conversacion): string {
    if (c.requerimientoEstado === 'FINALIZADO') {
      return 'Este trabajo fue marcado como completado. La conversación está cerrada.';
    }
    if (c.requerimientoEstado === 'CANCELADO') {
      return 'Este trabajo fue cancelado. La conversación está cerrada.';
    }
    return 'La conversación está cerrada.';
  }
}
