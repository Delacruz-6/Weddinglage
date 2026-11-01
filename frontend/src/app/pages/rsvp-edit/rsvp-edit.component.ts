import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';
import { SnowComponent } from '../../shared/components/snow/snow.component';

@Component({
  selector: 'app-rsvp-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, SnowComponent],
  templateUrl: './rsvp-edit.component.html',
  styleUrl: './rsvp-edit.component.scss'
})
export class RsvpEditComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PublicApiService);
  private readonly route = inject(ActivatedRoute);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly saved = signal(false);
  readonly inviteCode = signal('');
  private editToken = '';

  readonly form = this.fb.nonNullable.group({
    attending: [true, Validators.required],
    adults: [1, [Validators.required, Validators.min(0)]],
    children: [0, [Validators.required, Validators.min(0)]],
    notes: [''],
    dietaryRequirements: this.fb.array([
      this.fb.nonNullable.group({ type: [''], detail: [''] })
    ]),
    transport: this.fb.array([
      this.fb.nonNullable.group({ busSlot: [''], stopId: [''], seats: [0 as number] })
    ]),
    songs: this.fb.array([
      this.fb.nonNullable.group({ title: [''], artist: [''] })
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
    this.editToken = this.route.snapshot.paramMap.get('editToken') ?? '';
    if (!this.editToken) {
      this.loading.set(false);
      this.error.set('Token de edición no válido.');
      return;
    }
    this.api.getRsvp(this.editToken).subscribe({
      next: (rsvp) => {
        this.inviteCode.set(rsvp.inviteCode);
        this.form.patchValue({
          attending: rsvp.attending,
          adults: rsvp.adults,
          children: rsvp.children,
          notes: rsvp.notes ?? ''
        });
        if (rsvp.dietaryRequirements?.length) {
          this.dietary.clear();
          for (const d of rsvp.dietaryRequirements) {
            this.dietary.push(
              this.fb.nonNullable.group({ type: [d.type], detail: [d.detail ?? ''] })
            );
          }
        }
        if (rsvp.transport?.length) {
          this.transport.clear();
          for (const t of rsvp.transport) {
            this.transport.push(
              this.fb.nonNullable.group({
                busSlot: [t.busSlot ?? ''],
                stopId: [t.stopId ?? ''],
                seats: [t.seats]
              })
            );
          }
        }
        if (rsvp.songs?.length) {
          this.songs.clear();
          for (const s of rsvp.songs) {
            this.songs.push(
              this.fb.nonNullable.group({ title: [s.title], artist: [s.artist ?? ''] })
            );
          }
        }
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No se pudo cargar el RSVP.');
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.saving.set(true);
    this.error.set(null);
    this.saved.set(false);

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
      .updateRsvp(this.editToken, {
        attending: v.attending,
        adults: v.adults,
        children: v.children,
        notes: v.notes.trim() || null,
        dietaryRequirements: dietary.length ? dietary : null,
        transport: transport.length ? transport : null,
        songs: songs.length ? songs : null
      })
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.saved.set(true);
        },
        error: () => {
          this.saving.set(false);
          this.error.set('No se pudo guardar. Intentadlo de nuevo.');
        }
      });
  }
}
