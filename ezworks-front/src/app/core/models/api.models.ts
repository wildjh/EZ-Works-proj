export type RolCodigo = 'EMPLEADOR' | 'AYUDANTE' | 'ADMIN';
export type EstadoRequerimiento = 'BORRADOR' | 'PUBLICADO' | 'EN_MATCH' | 'FINALIZADO' | 'CANCELADO';
export type EstadoPostulacion = 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA' | 'RETIRADA';

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
  email: string;
  roles: string[];
  mensaje: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  roles: RolCodigo[];
  aceptaTerminos: boolean;
}

export interface PerfilEmpleadorDto {
  id: number;
  calificacionPromedio: number;
  totalResenas: number;
}

export interface PerfilAyudanteDto {
  id: number;
  bio?: string;
  calificacionPromedio: number;
  totalResenas: number;
}

export interface UsuarioResponse {
  id: number;
  email: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  fotoPerfilUrl?: string;
  estadoCuenta: string;
  saldoDeudaAcumulado?: number;
  roles: RolCodigo[];
  perfilEmpleador?: PerfilEmpleadorDto;
  perfilAyudante?: PerfilAyudanteDto;
  creadoEn: string;
}

export type EstadoCuenta = 'ACTIVO' | 'SUSPENDIDO' | 'BANEADO' | 'INHABILITADO_DEUDA';

export interface AdminUsuario {
  id: number;
  email: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  fotoPerfilUrl?: string;
  estadoCuenta: EstadoCuenta;
  roles: RolCodigo[];
  creadoEn: string;
}

export interface PerfilAyudantePublico {
  id: number;
  usuarioId: number;
  nombre: string;
  apellido: string;
  bio?: string;
  fotoPerfilUrl?: string;
  calificacionPromedio: number;
  totalResenas: number;
  evidencias: Evidencia[];
}

export interface PerfilEmpleadorPublico {
  id: number;
  usuarioId: number;
  nombre: string;
  apellido: string;
  fotoPerfilUrl?: string;
  calificacionPromedio: number;
  totalResenas: number;
}

export interface UpdatePerfilRequest {
  nombre?: string;
  apellido?: string;
  telefono?: string;
  bio?: string;
}

export interface Categoria {
  id: number;
  nombre: string;
  activa: boolean;
}

export interface Requerimiento {
  id: number;
  titulo: string;
  descripcion: string;
  remuneracion: number;
  estado: EstadoRequerimiento;
  categoriaId: number;
  categoriaNombre: string;
  empleadorId: number;
  zonaAproximada?: string;
  direccionExacta?: string;
  publicadoEn?: string;
  actualizadoEn?: string;
  emparejamientoId?: number;
}

export interface RequerimientoRequest {
  categoriaId: number;
  titulo: string;
  descripcion: string;
  remuneracion: number;
  zonaAproximada?: string;
  latitudAprox?: number;
  longitudAprox?: number;
  direccionExacta?: string;
  latitudExacta?: number;
  longitudExacta?: number;
}

export interface Postulacion {
  id: number;
  requerimientoId: number;
  ayudanteId: number;
  ayudanteNombre: string;
  ayudanteApellido: string;
  ayudanteFotoPerfilUrl?: string;
  mensajePresentacion?: string;
  estado: EstadoPostulacion;
  creadoEn: string;
}

export interface Emparejamiento {
  id: number;
  requerimientoId: number;
  postulacionId: number;
  conversacionId: number;
  establecidoEn: string;
}

export interface Mensaje {
  id: number;
  conversacionId: number;
  emisorUsuarioId: number;
  emisorNombre: string;
  contenido: string;
  leido: boolean;
  enviadoEn: string;
}

export interface Conversacion {
  id: number;
  emparejamientoId: number;
  requerimientoId?: number;
  requerimientoEstado?: EstadoRequerimiento;
  activa?: boolean;
  abiertaEn: string;
  requerimientoTitulo?: string;
  otroParticipanteNombre?: string;
  otroParticipanteApellido?: string;
  otroParticipanteFotoUrl?: string;
  otroParticipantePerfilAyudanteId?: number;
  otroParticipantePerfilEmpleadorId?: number;
  empleadorUsuarioId?: number;
  empleadorEnvioPrimerMensaje?: boolean;
  puedeEnviar?: boolean;
  mensajes: Mensaje[];
}

export interface ConversacionResumen {
  id: number;
  requerimientoId?: number;
  requerimientoEstado?: EstadoRequerimiento;
  activa?: boolean;
  requerimientoTitulo: string;
  otroParticipanteNombre: string;
  otroParticipanteApellido: string;
  otroParticipanteFotoUrl?: string;
  ultimoMensaje?: string;
  abiertaEn: string;
}

export type TipoEvidencia = 'FOTO' | 'DOCUMENTO';

export interface Evidencia {
  id: number;
  urlArchivo: string;
  tipo: TipoEvidencia;
  descripcion?: string;
  subidoEn: string;
}

export interface ProblemDetail {
  title?: string;
  detail?: string;
  status?: number;
  errors?: Record<string, string>;
}

export type EstadoDeuda = 'PENDIENTE' | 'PAGADA' | 'VENCIDA';
export type TipoMetodoPago = 'TARJETA' | 'NEQUI' | 'DAVIPLATA' | 'OTRO';
export type TipoTransaccion = 'COMISION_MATCH' | 'PAGO_DEUDA' | 'PAGO_TRABAJO';
export type EstadoTransaccion = 'PENDIENTE' | 'COMPLETADA' | 'FALLIDA';

export interface Deuda {
  id: number;
  monto: number;
  estado: EstadoDeuda;
  descripcion: string;
  requerimientoTitulo?: string;
  creadoEn: string;
  pagadaEn?: string;
}

export interface MetodoPago {
  id: number;
  tipo: TipoMetodoPago;
  alias: string;
  ultimosCuatro?: string;
  predeterminado: boolean;
  creadoEn: string;
}

export interface TransaccionPago {
  id: number;
  tipo: TipoTransaccion;
  monto: number;
  estado: EstadoTransaccion;
  referencia?: string;
  creadoEn: string;
  completadaEn?: string;
}

export interface MiDeudaResumen {
  saldoDeudaAcumulado: number;
  limiteDeudaMaxima: number;
  inhabilitadoPorDeuda: boolean;
  deudasPendientes: Deuda[];
  historialDeudas: Deuda[];
  transaccionesRecientes: TransaccionPago[];
}

export interface MetodoPagoRequest {
  tipo: TipoMetodoPago;
  alias: string;
  ultimosCuatro?: string;
  predeterminado?: boolean;
}
