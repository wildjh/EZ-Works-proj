import { Component, input } from '@angular/core';

@Component({
  selector: 'app-user-avatar',
  standalone: true,
  template: `
    <div class="avatar" [class.avatar-sm]="size() === 'sm'" [class.avatar-md]="size() === 'md'">
      @if (url()) {
        <img [src]="url()" [alt]="name() ?? 'Usuario'" />
      } @else {
        <span class="avatar-initials">{{ initials() }}</span>
      }
    </div>
  `,
  styles: [
    `
      .avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        overflow: hidden;
        background: var(--primary-soft);
        border: 2px solid var(--border);
        display: inline-flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }
      .avatar-sm {
        width: 32px;
        height: 32px;
      }
      .avatar-md {
        width: 48px;
        height: 48px;
      }
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      .avatar-initials {
        font-size: 0.75rem;
        font-weight: 700;
        color: var(--primary);
      }
    `,
  ],
})
export class UserAvatarComponent {
  readonly url = input<string | null>(null);
  readonly name = input<string | null>(null);
  readonly size = input<'sm' | 'md' | 'lg'>('md');

  initials(): string {
    const n = this.name()?.trim();
    if (!n) return '?';
    const parts = n.split(/\s+/).filter(Boolean);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return parts[0].slice(0, 2).toUpperCase();
  }
}
