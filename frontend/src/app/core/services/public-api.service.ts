import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  OrnamentPublic,
  PhotoPublic,
  PhotoUploadResponse,
  PlaylistTrack,
  PublicWedding,
  RsvpSubmitRequest,
  RsvpSubmitResponse,
  RsvpUpdateRequest,
  RsvpView,
  TimelineEvent,
  Venue
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class PublicApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/public';

  getWedding(): Observable<PublicWedding> {
    return this.http.get<PublicWedding>(`${this.base}/wedding`);
  }

  getVenue(): Observable<Venue> {
    return this.http.get<Venue>(`${this.base}/venue`);
  }

  getTimeline(): Observable<TimelineEvent[]> {
    return this.http.get<TimelineEvent[]>(`${this.base}/timeline`);
  }

  getPlaylist(): Observable<PlaylistTrack[]> {
    return this.http.get<PlaylistTrack[]>(`${this.base}/playlist`);
  }

  submitRsvp(body: RsvpSubmitRequest): Observable<RsvpSubmitResponse> {
    return this.http.post<RsvpSubmitResponse>(`${this.base}/rsvp`, body);
  }

  getRsvp(editToken: string): Observable<RsvpView> {
    return this.http.get<RsvpView>(`${this.base}/rsvp/${encodeURIComponent(editToken)}`);
  }

  updateRsvp(editToken: string, body: RsvpUpdateRequest): Observable<RsvpView> {
    return this.http.put<RsvpView>(`${this.base}/rsvp/${encodeURIComponent(editToken)}`, body);
  }

  getOrnament(inviteCode: string): Observable<OrnamentPublic> {
    return this.http.get<OrnamentPublic>(
      `${this.base}/ornaments/${encodeURIComponent(inviteCode)}`
    );
  }

  listPhotos(): Observable<PhotoPublic[]> {
    return this.http.get<PhotoPublic[]>(`${this.base}/photos`);
  }

  photoContentUrl(id: number): string {
    return `${this.base}/photos/${id}/content`;
  }

  uploadPhoto(file: File, uploadToken: string): Observable<PhotoUploadResponse> {
    const form = new FormData();
    form.append('file', file);
    const headers = new HttpHeaders({ 'X-Upload-Token': uploadToken });
    return this.http.post<PhotoUploadResponse>(`${this.base}/photos`, form, { headers });
  }
}
