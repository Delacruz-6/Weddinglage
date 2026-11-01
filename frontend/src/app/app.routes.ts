import { Routes } from '@angular/router';
import { adminAuthGuard } from './core/guards/admin-auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then((m) => m.HomeComponent)
  },
  {
    path: 'rsvp/edit/:editToken',
    loadComponent: () =>
      import('./pages/rsvp-edit/rsvp-edit.component').then((m) => m.RsvpEditComponent)
  },
  {
    path: 'rsvp',
    loadComponent: () => import('./pages/rsvp/rsvp.component').then((m) => m.RsvpComponent)
  },
  { path: 'rsvp/:legacyCode', redirectTo: 'rsvp' },
  { path: 'ornament', redirectTo: '' },
  { path: 'ornament/:legacyCode', redirectTo: '' },
  {
    path: 'fotos',
    loadComponent: () => import('./pages/fotos/fotos.component').then((m) => m.FotosComponent)
  },
  {
    path: 'upload',
    loadComponent: () => import('./pages/upload/upload.component').then((m) => m.UploadComponent)
  },
  {
    path: 'gallery',
    loadComponent: () =>
      import('./pages/gallery/gallery.component').then((m) => m.GalleryComponent)
  },
  {
    path: 'admin/login',
    loadComponent: () =>
      import('./pages/admin/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'admin',
    canActivate: [adminAuthGuard],
    loadComponent: () =>
      import('./shared/components/admin-shell/admin-shell.component').then(
        (m) => m.AdminShellComponent
      ),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/admin/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      {
        path: 'configuration',
        loadComponent: () =>
          import('./pages/admin/configuration/configuration.component').then(
            (m) => m.ConfigurationComponent
          )
      },
      { path: 'invitations', redirectTo: 'exports', pathMatch: 'full' },
      {
        path: 'photos',
        loadComponent: () =>
          import('./pages/admin/photos/photos.component').then((m) => m.PhotosComponent)
      },
      {
        path: 'timeline',
        loadComponent: () =>
          import('./pages/admin/timeline/timeline.component').then((m) => m.TimelineComponent)
      },
      {
        path: 'playlist',
        loadComponent: () =>
          import('./pages/admin/playlist/playlist.component').then((m) => m.PlaylistComponent)
      },
      {
        path: 'exports',
        loadComponent: () =>
          import('./pages/admin/exports/exports.component').then((m) => m.ExportsComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
