import { CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';
import { Conversacion, Postulacion, Requerimiento } from '../../../core/models/api.models';
import { UserAvatarComponent } from '../../../core/components/user-avatar/user-avatar.component';
import { assetUrl } from '../../../core/utils/asset-url';

@Component({
  selector: 'app-requerimiento-detalle',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, ReactiveFormsModule, UserAvatarComponent],
  templateUrl: './requerimiento-detalle.component.html',
})
export class RequerimientoDetalleComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthService);

  readonly req = signal<Requerimiento | null>(null);
  readonly postulaciones = signal<Postulacion[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly actionMsg = signal('');
  readonly conv = signal<Conversacion | null>(null);
  readonly sending = signal(false);
  readonly chatError = signal('');

  readonly chatForm = this.fb.nonNullable.group({ contenido: [''] });

  private id = 0;

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.api.getRequerimiento(this.id).subscribe({
      next: (r) => {
        this.req.set(r);
        this.loading.set(false);
        if (r.estado === 'PUBLICADO' || r.estado === 'EN_MATCH') {
          this.loadPostulaciones();
        }
        if (r.emparejamientoId) {
          this.loadConversacion(r.emparejamientoId);
        } else {
          this.conv.set(null);
        }
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'No encontrado');
        this.loading.set(false);
      },
    });
  }

  loadPostulaciones(): void {
    this.api.getPostulaciones(this.id).subscribe({
      next: (p) => this.postulaciones.set(p),
    });
  }

  loadConversacion(emparejamientoId: number): void {
    this.api.getConversacionPorEmparejamiento(emparejamientoId).subscribe({
      next: (c) => this.conv.set(c),
    });
  }

  mostrarChatInicial(): boolean {
    const c = this.conv();
    return !!c && !c.empleadorEnvioPrimerMensaje && this.auth.hasRole('EMPLEADOR');
  }

  publicar(): void {
    this.api.publicarRequerimiento(this.id).subscribe({
      next: (r) => {
        this.req.set(r);
        this.actionMsg.set('Publicado correctamente');
        this.loadPostulaciones();
      },
      error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo publicar'),
    });
  }

  match(postulacionId: number): void {
    this.api.crearMatch(this.id, postulacionId).subscribe({
      next: (m) => {
        this.actionMsg.set('Match realizado. Envía el primer mensaje al ayudante.');
        this.load();
        this.api.getConversacion(m.conversacionId).subscribe({
          next: (c) => this.conv.set(c),
        });
      },
      error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo hacer match'),
    });
  }

  enviarPrimerMensaje(): void {
    const texto = this.chatForm.value.contenido?.trim();
    const c = this.conv();
    if (!texto || !c) return;

    this.sending.set(true);
    this.chatError.set('');
    this.api.enviarMensaje(c.id, texto).subscribe({
      next: () => {
        this.chatForm.reset();
        this.sending.set(false);
        this.conv.set(null);
        this.actionMsg.set('Mensaje enviado. Continúa la conversación en Chats.');
      },
      error: (err) => {
        this.sending.set(false);
        this.chatError.set(err?.error?.detail ?? 'Error al enviar');
      },
    });
  }

  fotoPostulante(p: Postulacion): string | null {
    return assetUrl(p.ayudanteFotoPerfilUrl);
  }

  eliminarBorrador(): void {
    if (!confirm('¿Eliminar este borrador?')) return;
    this.api.eliminarRequerimiento(this.id).subscribe({
      next: () => this.router.navigate(['/empleador/requerimientos']),
      error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo eliminar'),
    });
  }

  finalizarTrabajo(): void {
    if (!confirm('¿Marcar este trabajo como completado? Se cerrará la conversación del chat.')) return;
    this.api.finalizarRequerimiento(this.id).subscribe({
      next: (r) => {
        this.req.set(r);
        this.conv.set(null);
        this.actionMsg.set('Trabajo completado. La conversación del chat quedó cerrada.');
      },
      error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo finalizar'),
    });
  }

  cancelarTrabajo(): void {
    const r = this.req();
    const msg =
      r?.estado === 'EN_MATCH'
        ? '¿Cancelar este trabajo? Se cerrará el match y la conversación.'
        : '¿Cancelar esta vacante publicada?';
    if (!confirm(msg)) return;

    this.api.cancelarRequerimiento(this.id).subscribe({
      next: (updated) => {
        this.req.set(updated);
        this.conv.set(null);
        this.actionMsg.set('Trabajo cancelado correctamente.');
      },
      error: (err) => this.error.set(err?.error?.detail ?? 'No se pudo cancelar'),
    });
  }
}
