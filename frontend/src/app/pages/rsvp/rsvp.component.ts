import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
import { SnowComponent } from '../../shared/components/snow/snow.component';

@Component({
  selector: 'app-rsvp',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, SnowComponent],
  templateUrl: './rsvp.component.html',
  styleUrl: './rsvp.component.scss'
})
export class RsvpComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PublicApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);
  readonly successToken = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    inviteCode: ['', Validators.required],
    attending: [true, Validators.required],
    adults: [1, [Validators.required, Validators.min(0)]],
    children: [0, [Validators.required, Validators.min(0)]],
    notes: [''],
    dietaryRequirements: this.fb.array([
      this.fb.nonNullable.group({
        type: [''],
        detail: ['']
      })
    ]),
    transport: this.fb.array([
      this.fb.nonNullable.group({
        busSlot: [''],
        stopId: [''],
        seats: [0 as number]
      })
    ]),
    songs: this.fb.array([
      this.fb.nonNullable.group({
        title: [''],
        artist: ['']
      })
    ])
  });

  get dietary(): FormArray {
    return this.form.controls.dietaryRequirements;
  }

  get transport(): FormArray {
    return this.form.controls.transport;
  }

  get songs(): FormArray {
    return this.form.controls.songs;
  }

  ngOnInit(): void {
    const fromPath = this.route.snapshot.paramMap.get('inviteCode');
    const fromQuery = this.route.snapshot.queryParamMap.get('inviteCode');
    const code = fromPath || fromQuery;
    if (code) {
      this.form.patchValue({ inviteCode: code });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.submitting.set(true);
    this.error.set(null);

    const dietary = v.dietaryRequirements
      .filter((d) => d.type.trim())
      .map((d) => ({ type: d.type.trim(), detail: d.detail.trim() || null }));
    const transport = v.transport
      .filter((t) => t.busSlot.trim() || t.stopId.trim() || t.seats > 0)
      .map((t) => ({
        busSlot: t.busSlot.trim() || null,
        stopId: t.stopId.trim() || null,
        seats: t.seats || null
      }));
    const songs = v.songs
      .filter((s) => s.title.trim())
      .map((s) => ({ title: s.title.trim(), artist: s.artist.trim() || null }));

    this.api
      .submitRsvp({
        inviteCode: v.inviteCode.trim(),
        attending: v.attending,
        adults: v.adults,
        children: v.children,
        notes: v.notes.trim() || null,
        dietaryRequirements: dietary.length ? dietary : null,
        transport: transport.length ? transport : null,
        songs: songs.length ? songs : null
      })
      .subscribe({
        next: (res) => {
          this.submitting.set(false);
          this.successToken.set(res.editToken);
        },
        error: () => {
          this.submitting.set(false);
          this.error.set('No se pudo enviar la confirmación. Revisad el código e intentadlo de nuevo.');
        }
      });
  }

  goEdit(): void {
    const token = this.successToken();
    if (token) {
      void this.router.navigate(['/rsvp/edit', token]);
    }
  }
}
