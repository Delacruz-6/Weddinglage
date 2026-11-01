export interface SectionFlags {
  story: boolean;
  venue: boolean;
  timeline: boolean;
  playlist: boolean;
  gifts: boolean;
  rsvp: boolean;
  photos: boolean;
  ornaments: boolean;
}

export interface PublicWedding {
  partnerNames: string | null;
  weddingAt: string | null;
  welcomeMessage: string | null;
  storyHtml: string | null;
  giftIban: string | null;
  giftHolder: string | null;
  sections: SectionFlags;
}

export interface WeddingConfiguration {
  partnerNames: string | null;
  weddingAt: string | null;
  welcomeMessage: string | null;
  storyHtml: string | null;
  giftIban: string | null;
  giftHolder: string | null;
  showStory: boolean;
  showVenue: boolean;
  showTimeline: boolean;
  showPlaylist: boolean;
  showGifts: boolean;
  showRsvp: boolean;
  showPhotos: boolean;
  showOrnaments: boolean;
}

export interface VenueImage {
  id: number;
  imageUrl: string;
  altText: string | null;
  sortOrder: number;
}

export interface Venue {
  name: string | null;
  addressLine: string | null;
  city: string | null;
  postalCode: string | null;
  country: string | null;
  latitude: number | null;
  longitude: number | null;
  howToArrive: string | null;
  parkingNotes: string | null;
  images: VenueImage[] | null;
}

export interface TimelineEvent {
  id: number | null;
  eventTime: string | null;
  title: string | null;
  description: string | null;
  icon: string | null;
  sortOrder: number;
}

export interface PlaylistTrack {
  id: number | null;
  title: string | null;
  artist: string | null;
  sourceUrl: string | null;
  licenseNote: string | null;
  sortOrder: number;
}

export interface DietaryRequirement {
  type: string;
  detail: string | null;
}

export interface TransportRequest {
  busSlot: string | null;
  stopId: string | null;
  seats: number | null;
}

export interface TransportView {
  busSlot: string | null;
  stopId: string | null;
  status: string | null;
  seats: number;
}

export interface SongRequest {
  title: string;
  artist: string | null;
}

export interface RsvpSubmitRequest {
  inviteCode: string;
  attending: boolean;
  adults: number;
  children: number;
  notes: string | null;
  dietaryRequirements: DietaryRequirement[] | null;
  transport: TransportRequest[] | null;
  songs: SongRequest[] | null;
}

export interface RsvpUpdateRequest {
  attending: boolean;
  adults: number;
  children: number;
  notes: string | null;
  dietaryRequirements: DietaryRequirement[] | null;
  transport: TransportRequest[] | null;
  songs: SongRequest[] | null;
}

export interface RsvpView {
  inviteCode: string;
  attending: boolean;
  adults: number;
  children: number;
  notes: string | null;
  submittedAt: string | null;
  updatedAt: string | null;
  dietaryRequirements: DietaryRequirement[] | null;
  transport: TransportView[] | null;
  songs: SongRequest[] | null;
}

export interface RsvpSubmitResponse {
  editToken: string;
  rsvp: RsvpView;
}

export interface OrnamentPublic {
  inviteCode: string;
  ornamentCode: string | null;
  ornamentName: string | null;
  imageKey: string | null;
}

export interface PhotoPublic {
  id: number;
  mimeType: string;
  uploadedAt: string;
}

export interface PhotoUploadResponse {
  id: number;
  status: string;
}

export interface PhotoAdmin {
  id: number;
  originalName: string | null;
  mimeType: string;
  status: string;
  uploadedAt: string;
  reviewedAt: string | null;
  reviewedBy: string | null;
}

export interface PhotoPatchRequest {
  status: string;
}

export interface UploadTokenRequest {
  label: string | null;
  expiresAt: string | null;
}

export interface UploadTokenResponse {
  id: number;
  token: string;
  expiresAt: string | null;
}

export interface PublicationConfig {
  publishAt: string | null;
  adminAuthorized: boolean;
  authorizedBy: string | null;
  authorizedAt: string | null;
}

export interface PublicationConfigUpdateRequest {
  publishAt: string | null;
  adminAuthorized: boolean | null;
}

export interface InvitationRequest {
  inviteCode: string;
  label: string | null;
  maxAttendees: number;
  active: boolean | null;
}

export interface Invitation {
  id: number;
  inviteCode: string;
  label: string | null;
  maxAttendees: number;
  active: boolean;
  hasRsvp: boolean;
  ornamentCode: string | null;
}

export interface AssignOrnamentRequest {
  ornamentCode: string | null;
  random: boolean | null;
}

export interface RegenerateTokenResponse {
  editToken: string;
}

export interface Dashboard {
  invitationsTotal: number;
  invitationsActive: number;
  rsvpTotal: number;
  rsvpAttending: number;
  rsvpDeclined: number;
  guestsExpected: number;
  photosPending: number;
  photosApproved: number;
  photosRejected: number;
  galleryPublished: boolean;
  ornamentsAssigned: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface CsrfResponse {
  headerName: string;
  parameterName: string;
  token: string;
}
