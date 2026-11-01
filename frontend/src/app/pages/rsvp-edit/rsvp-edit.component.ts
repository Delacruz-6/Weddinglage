import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

type ChildGuestForm = FormGroup<{
  fullName: FormControl<string>;
  age: FormControl<number | null>;
  intolerances: FormControl<string>;
}>;
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PublicApiService } from '../../core/services/public-api.service';

@Component({
  selector: 'app-rsvp-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
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
  readonly displayName = signal<string | null>(null);
  private editToken = '';

  readonly form = this.fb.nonNullable.group({
    fullName: [''],
    attending: [true, Validators.required],
    adults: [1, [Validators.required, Validators.min(0)]],
    notes: [''],
    childGuests: this.fb.array<ChildGuestForm>([]),
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

  get childGuests(): FormArray<ChildGuestForm> {
    return this.form.controls.childGuests;
  }

  private newChildGroup(
    fullName = '',
    age: number | null = null,
    intolerances = ''
  ): ChildGuestForm {
    return this.fb.group({
      fullName: this.fb.nonNullable.control(fullName, Validators.required),
      age: this.fb.control<number | null>(age),
      intolerances: this.fb.nonNullable.control(intolerances)
    });
  }

  addDietary(): void {
    this.dietary.push(this.fb.nonNullable.group({ type: [''], detail: [''] }));
  }

  removeDietary(index: number): void {
    if (this.dietary.length <= 1) {
      this.dietary.at(0).reset({ type: '', detail: '' });
      return;
    }
    this.dietary.removeAt(index);
  }

  addChild(): void {
    this.childGuests.push(this.newChildGroup());
  }

  removeChild(index: number): void {
    this.childGuests.removeAt(index);
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
        this.displayName.set(rsvp.fullName);
        this.form.patchValue({
          fullName: rsvp.fullName ?? '',
          attending: rsvp.attending,
          adults: rsvp.adults,
          notes: rsvp.notes ?? ''
        });
        if (rsvp.childGuests?.length) {
          this.childGuests.clear();
          for (const c of rsvp.childGuests) {
            this.childGuests.push(this.newChildGroup(c.fullName, c.age, c.intolerances ?? ''));
          }
        }
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
        this.error.set('No se pudo cargar la confirmación.');
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

    const childrenList = v.childGuests
      .filter((c) => c.fullName.trim())
      .map((c) => ({
        fullName: c.fullName.trim(),
        age: c.age == null || Number.isNaN(Number(c.age)) ? null : Number(c.age),
        intolerances: c.intolerances.trim() || null
      }));

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
        fullName: v.fullName.trim() || null,
        attending: v.attending,
        adults: v.adults,
        children: childrenList.length,
        notes: v.notes.trim() || null,
        dietaryRequirements: dietary.length ? dietary : null,
        transport: transport.length ? transport : null,
        songs: songs.length ? songs : null,
        childGuests: childrenList.length ? childrenList : null
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
