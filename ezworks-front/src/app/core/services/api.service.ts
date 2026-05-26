import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  AdminUsuario,
  Categoria,
  Conversacion,
  ConversacionResumen,
  Deuda,
  Emparejamiento,
  EstadoCuenta,
  Evidencia,
  Mensaje,
  MetodoPago,
  MetodoPagoRequest,
  MiDeudaResumen,
  PerfilAyudantePublico,
  PerfilEmpleadorPublico,
  Postulacion,
  Requerimiento,
  RequerimientoRequest,
  TipoEvidencia,
  UpdatePerfilRequest,
  UsuarioResponse,
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getCategorias() {
    return this.http.get<Categoria[]>(`${this.api}/api/categorias`);
  }

  getMe() {
    return this.http.get<UsuarioResponse>(`${this.api}/api/usuarios/me`);
  }

  updateMe(body: UpdatePerfilRequest) {
    return this.http.patch<UsuarioResponse>(`${this.api}/api/usuarios/me`, body);
  }

  uploadFotoPerfil(archivo: File) {
    const form = new FormData();
    form.append('archivo', archivo);
    return this.http.post<UsuarioResponse>(`${this.api}/api/usuarios/me/foto-perfil`, form);
  }

  getMisEvidencias() {
    return this.http.get<Evidencia[]>(`${this.api}/api/evidencias/mis`);
  }

  subirEvidencia(archivo: File, descripcion?: string, tipo: TipoEvidencia = 'FOTO') {
    const form = new FormData();
    form.append('archivo', archivo);
    if (descripcion) form.append('descripcion', descripcion);
    form.append('tipo', tipo);
    return this.http.post<Evidencia>(`${this.api}/api/evidencias`, form);
  }

  eliminarEvidencia(id: number) {
    return this.http.delete<void>(`${this.api}/api/evidencias/${id}`);
  }

  getVacantes() {
    return this.http.get<Requerimiento[]>(`${this.api}/api/requerimientos/vacantes`);
  }

  getMisRequerimientos() {
    return this.http.get<Requerimiento[]>(`${this.api}/api/requerimientos/mis`);
  }

  getRequerimiento(id: number) {
    return this.http.get<Requerimiento>(`${this.api}/api/requerimientos/${id}`);
  }

  crearRequerimiento(body: RequerimientoRequest) {
    return this.http.post<Requerimiento>(`${this.api}/api/requerimientos`, body);
  }

  actualizarRequerimiento(id: number, body: RequerimientoRequest) {
    return this.http.put<Requerimiento>(`${this.api}/api/requerimientos/${id}`, body);
  }

  publicarRequerimiento(id: number) {
    return this.http.post<Requerimiento>(`${this.api}/api/requerimientos/${id}/publicar`, {});
  }

  postular(requerimientoId: number, mensajePresentacion?: string) {
    return this.http.post<Postulacion>(
      `${this.api}/api/requerimientos/${requerimientoId}/postulaciones`,
      { mensajePresentacion: mensajePresentacion ?? '' }
    );
  }

  getPostulaciones(requerimientoId: number) {
    return this.http.get<Postulacion[]>(
      `${this.api}/api/requerimientos/${requerimientoId}/postulaciones`
    );
  }

  crearMatch(requerimientoId: number, postulacionId: number) {
    return this.http.post<Emparejamiento>(
      `${this.api}/api/requerimientos/${requerimientoId}/match`,
      { postulacionId }
    );
  }

  finalizarRequerimiento(id: number) {
    return this.http.post<Requerimiento>(`${this.api}/api/requerimientos/${id}/finalizar`, {});
  }

  cancelarRequerimiento(id: number) {
    return this.http.post<Requerimiento>(`${this.api}/api/requerimientos/${id}/cancelar`, {});
  }

  eliminarRequerimiento(id: number) {
    return this.http.delete<void>(`${this.api}/api/requerimientos/${id}`);
  }

  getPerfilAyudantePublico(perfilAyudanteId: number) {
    return this.http.get<PerfilAyudantePublico>(
      `${this.api}/api/perfiles/ayudante/${perfilAyudanteId}`
    );
  }

  getPerfilEmpleadorPublico(perfilEmpleadorId: number) {
    return this.http.get<PerfilEmpleadorPublico>(
      `${this.api}/api/perfiles/empleador/${perfilEmpleadorId}`
    );
  }

  getAdminUsuarios() {
    return this.http.get<AdminUsuario[]>(`${this.api}/api/admin/usuarios`);
  }

  actualizarEstadoUsuario(id: number, estadoCuenta: EstadoCuenta, motivo?: string) {
    return this.http.patch<AdminUsuario>(`${this.api}/api/admin/usuarios/${id}/estado`, {
      estadoCuenta,
      motivo,
    });
  }

  getConversacion(id: number) {
    return this.http.get<Conversacion>(`${this.api}/api/conversaciones/${id}`);
  }

  getMisConversaciones() {
    return this.http.get<ConversacionResumen[]>(`${this.api}/api/conversaciones`);
  }

  getConversacionPorEmparejamiento(emparejamientoId: number) {
    return this.http.get<Conversacion>(
      `${this.api}/api/conversaciones/emparejamiento/${emparejamientoId}`
    );
  }

  enviarMensaje(conversacionId: number, contenido: string) {
    return this.http.post<Mensaje>(
      `${this.api}/api/conversaciones/${conversacionId}/mensajes`,
      { contenido }
    );
  }

  getMiDeuda() {
    return this.http.get<MiDeudaResumen>(`${this.api}/api/pagos/mi-deuda`);
  }

  getMetodosPago() {
    return this.http.get<MetodoPago[]>(`${this.api}/api/pagos/metodos`);
  }

  registrarMetodoPago(body: MetodoPagoRequest) {
    return this.http.post<MetodoPago>(`${this.api}/api/pagos/metodos`, body);
  }

  pagarDeuda(deudaId: number, metodoPagoId: number) {
    return this.http.post<{ id: number }>(`${this.api}/api/pagos/deudas/${deudaId}/pagar`, {
      metodoPagoId,
    });
  }
}
