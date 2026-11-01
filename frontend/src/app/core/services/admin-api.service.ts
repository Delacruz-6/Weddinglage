import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AssignOrnamentRequest,
  CsrfResponse,
  Dashboard,
  Invitation,
  InvitationRequest,
  LoginRequest,
  OrnamentPublic,
  PhotoAdmin,
  PhotoPatchRequest,
  PlaylistTrack,
  PublicationConfig,
  PublicationConfigUpdateRequest,
  RegenerateTokenResponse,
  TimelineEvent,
  UploadTokenRequest,
  UploadTokenResponse,
  Venue,
  WeddingConfiguration
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class AdminApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/admin';

  getCsrf(): Observable<CsrfResponse> {
    return this.http.get<CsrfResponse>(`${this.base}/auth/csrf`);
  }

  login(body: LoginRequest): Observable<Record<string, unknown>> {
    return this.http.post<Record<string, unknown>>(`${this.base}/auth/login`, body);
  }

  logout(): Observable<{ ok: boolean }> {
    return this.http.post<{ ok: boolean }>(`${this.base}/auth/logout`, {});
  }

  getDashboard(): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.base}/dashboard`);
  }

  getConfiguration(): Observable<WeddingConfiguration> {
    return this.http.get<WeddingConfiguration>(`${this.base}/configuration`);
  }

  updateConfiguration(body: WeddingConfiguration): Observable<WeddingConfiguration> {
    return this.http.put<WeddingConfiguration>(`${this.base}/configuration`, body);
  }

  listInvitations(): Observable<Invitation[]> {
    return this.http.get<Invitation[]>(`${this.base}/invitations`);
  }

  getInvitation(id: number): Observable<Invitation> {
    return this.http.get<Invitation>(`${this.base}/invitations/${id}`);
  }

  createInvitation(body: InvitationRequest): Observable<Invitation> {
    return this.http.post<Invitation>(`${this.base}/invitations`, body);
  }

  updateInvitation(id: number, body: InvitationRequest): Observable<Invitation> {
    return this.http.put<Invitation>(`${this.base}/invitations/${id}`, body);
  }

  deleteInvitation(id: number): Observable<{ ok: boolean }> {
    return this.http.delete<{ ok: boolean }>(`${this.base}/invitations/${id}`);
  }

  assignOrnament(id: number, body?: AssignOrnamentRequest): Observable<OrnamentPublic> {
    return this.http.post<OrnamentPublic>(`${this.base}/invitations/${id}/ornament`, body ?? {});
  }

  regenerateToken(id: number): Observable<RegenerateTokenResponse> {
    return this.http.post<RegenerateTokenResponse>(
      `${this.base}/invitations/${id}/regenerate-token`,
      {}
    );
  }

  listPhotos(): Observable<PhotoAdmin[]> {
    return this.http.get<PhotoAdmin[]>(`${this.base}/photos`);
  }

  patchPhoto(id: number, body: PhotoPatchRequest): Observable<PhotoAdmin> {
    return this.http.patch<PhotoAdmin>(`${this.base}/photos/${id}`, body);
  }

  deletePhoto(id: number): Observable<{ ok: boolean }> {
    return this.http.delete<{ ok: boolean }>(`${this.base}/photos/${id}`);
  }

  createUploadToken(body: UploadTokenRequest): Observable<UploadTokenResponse> {
    return this.http.post<UploadTokenResponse>(`${this.base}/photos/upload-token`, body);
  }

  getPublication(): Observable<PublicationConfig> {
    return this.http.get<PublicationConfig>(`${this.base}/publication`);
  }

  updatePublication(body: PublicationConfigUpdateRequest): Observable<PublicationConfig> {
    return this.http.put<PublicationConfig>(`${this.base}/publication`, body);
  }

  getVenue(): Observable<Venue> {
    return this.http.get<Venue>(`${this.base}/venue`);
  }

  updateVenue(body: Venue): Observable<Venue> {
    return this.http.put<Venue>(`${this.base}/venue`, body);
  }

  listTimeline(): Observable<TimelineEvent[]> {
    return this.http.get<TimelineEvent[]>(`${this.base}/timeline`);
  }

  createTimeline(body: TimelineEvent): Observable<TimelineEvent> {
    return this.http.post<TimelineEvent>(`${this.base}/timeline`, body);
  }

  updateTimeline(id: number, body: TimelineEvent): Observable<TimelineEvent> {
    return this.http.put<TimelineEvent>(`${this.base}/timeline/${id}`, body);
  }

  deleteTimeline(id: number): Observable<{ ok: boolean }> {
    return this.http.delete<{ ok: boolean }>(`${this.base}/timeline/${id}`);
  }

  listPlaylist(): Observable<PlaylistTrack[]> {
    return this.http.get<PlaylistTrack[]>(`${this.base}/playlist`);
  }

  createTrack(body: PlaylistTrack): Observable<PlaylistTrack> {
    return this.http.post<PlaylistTrack>(`${this.base}/playlist`, body);
  }

  updateTrack(id: number, body: PlaylistTrack): Observable<PlaylistTrack> {
    return this.http.put<PlaylistTrack>(`${this.base}/playlist/${id}`, body);
  }

  deleteTrack(id: number): Observable<{ ok: boolean }> {
    return this.http.delete<{ ok: boolean }>(`${this.base}/playlist/${id}`);
  }

  exportUrl(kind: 'guests' | 'catering' | 'transport' | 'songs' | 'ornaments'): string {
    return `${this.base}/exports/${kind}`;
  }
}
