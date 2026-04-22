import { Injectable, signal } from '@angular/core';
import Keycloak, { KeycloakProfile } from 'keycloak-js';

import { KEYCLOAK_CONFIG } from '../constants/auth.constants';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = new Keycloak({
    url: KEYCLOAK_CONFIG.url,
    realm: KEYCLOAK_CONFIG.realm,
    clientId: KEYCLOAK_CONFIG.clientId
  });

  private readonly authenticatedSignal = signal(false);
  private readonly userNameSignal = signal<string | null>(null);
  private readonly userEmailSignal = signal<string | null>(null);
  private readonly userRolesSignal = signal<string[]>([]);

  readonly authenticated = this.authenticatedSignal.asReadonly();
  readonly userName = this.userNameSignal.asReadonly();
  readonly userEmail = this.userEmailSignal.asReadonly();
  readonly userRoles = this.userRolesSignal.asReadonly();

  async initialize(): Promise<void> {
    if (!KEYCLOAK_CONFIG.enabled) {
      return;
    }

    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false
      });

      this.authenticatedSignal.set(authenticated);
      await this.updateUserProfile();
    } catch {
      this.authenticatedSignal.set(false);
      this.userNameSignal.set(null);
      this.userEmailSignal.set(null);
      this.userRolesSignal.set([]);
    }
  }

  isEnabled(): boolean {
    return KEYCLOAK_CONFIG.enabled;
  }

  isAuthenticated(): boolean {
    return this.authenticatedSignal();
  }

  hasRole(role: string): boolean {
    if (!role) {
      return false;
    }

    return this.userRolesSignal().includes(role.toLowerCase());
  }

  isAdmin(): boolean {
    return this.hasRole('hotel-admin');
  }

  async login(redirectUri?: string): Promise<void> {
    if (!KEYCLOAK_CONFIG.enabled) {
      return;
    }

    await this.keycloak.login({
      redirectUri: redirectUri ?? window.location.href
    });
  }

  async logout(redirectUri?: string): Promise<void> {
    if (!KEYCLOAK_CONFIG.enabled) {
      return;
    }

    await this.keycloak.logout({
      redirectUri: redirectUri ?? window.location.origin
    });
  }

  async getAccessToken(): Promise<string | undefined> {
    if (!KEYCLOAK_CONFIG.enabled || !this.keycloak.authenticated) {
      return undefined;
    }

    try {
      await this.keycloak.updateToken(30);
      return this.keycloak.token;
    } catch {
      return undefined;
    }
  }

  private async updateUserProfile(): Promise<void> {
    if (!this.keycloak.authenticated) {
      this.userNameSignal.set(null);
      this.userEmailSignal.set(null);
      this.userRolesSignal.set([]);
      return;
    }

    this.userEmailSignal.set(
      (this.keycloak.tokenParsed?.['email'] as string | undefined)
        ?? (this.keycloak.tokenParsed?.['preferred_username'] as string | undefined)
        ?? null
    );

    const realmRoles = (this.keycloak.tokenParsed?.['realm_access'] as { roles?: string[] } | undefined)?.roles ?? [];
    this.userRolesSignal.set(realmRoles.map((role) => role.toLowerCase()));

    try {
      const profile: KeycloakProfile = await this.keycloak.loadUserProfile();
      const displayName = profile.firstName || profile.username || profile.email || 'Utilisateur';
      this.userNameSignal.set(displayName);
    } catch {
      const preferredUserName = this.keycloak.tokenParsed?.['preferred_username'] as string | undefined;
      this.userNameSignal.set(preferredUserName ?? null);
    }
  }
}
