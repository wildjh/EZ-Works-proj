import { environment } from '../../../environments/environment';

export function assetUrl(path?: string | null): string | null {
  if (!path) return null;
  if (path.startsWith('http://') || path.startsWith('https://')) return path;
  return `${environment.apiUrl}${path}`;
}
