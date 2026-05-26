import { Injectable, inject } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';
import { Mensaje } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ChatRealtimeService {
  private readonly auth = inject(AuthService);
  private client: Client | null = null;

  subscribe(conversacionId: number, onMessage: (msg: Mensaje) => void): () => void {
    if (!conversacionId || conversacionId <= 0) {
      return () => undefined;
    }

    try {
      const client = this.ensureClient();
      const topic = `/topic/conversaciones/${conversacionId}`;
      let subscription: StompSubscription | null = null;

      const attach = () => {
        subscription = client.subscribe(topic, (frame: IMessage) => {
          try {
            onMessage(JSON.parse(frame.body) as Mensaje);
          } catch {
            // frame inválido
          }
        });
      };

      if (client.connected) {
        attach();
      } else {
        const priorOnConnect = client.onConnect;
        client.onConnect = (frame) => {
          priorOnConnect?.(frame);
          attach();
        };
      }

      return () => subscription?.unsubscribe();
    } catch {
      return () => undefined;
    }
  }

  disconnect(): void {
    this.client?.deactivate();
    this.client = null;
  }

  private ensureClient(): Client {
    if (this.client) return this.client;

    const token = this.auth.getToken();
    this.client = new Client({
      webSocketFactory: () => new SockJS(`${environment.apiUrl}/ws`) as WebSocket,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 4000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onStompError: () => {
        // El chat sigue funcionando vía REST aunque falle el WebSocket
      },
    });
    this.client.activate();
    return this.client;
  }
}
