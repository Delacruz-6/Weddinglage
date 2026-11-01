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
    path: 'rsvp/:inviteCode',
    loadComponent: () => import('./pages/rsvp/rsvp.component').then((m) => m.RsvpComponent)
  },
  {
    path: 'rsvp',
    loadComponent: () => import('./pages/rsvp/rsvp.component').then((m) => m.RsvpComponent)
  },
  {
    path: 'ornament/:inviteCode',
    loadComponent: () =>
      import('./pages/ornament/ornament.component').then((m) => m.OrnamentComponent)
  },
  {
    path: 'ornament',
    loadComponent: () =>
      import('./pages/ornament/ornament.component').then((m) => m.OrnamentComponent)
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
      {
        path: 'invitations',
        loadComponent: () =>
          import('./pages/admin/invitations/invitations.component').then(
            (m) => m.InvitationsComponent
          )
      },
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
        path: 'exports',
        loadComponent: () =>
          import('./pages/admin/exports/exports.component').then((m) => m.ExportsComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
