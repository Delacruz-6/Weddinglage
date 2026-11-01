import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RsvpFormConfig } from '../../core/models/api.models';
import { PublicApiService } from '../../core/services/public-api.service';

type ChildGuestForm = FormGroup<{
  fullName: FormControl<string>;
  age: FormControl<number | null>;
  intolerances: FormControl<string>;
}>;

const DEFAULT_FORM: RsvpFormConfig = {
  showNotes: true,
  showDietary: true,
  showTransport: true,
  showSongs: true,
  showChildren: true
};

@Component({
  selector: 'app-rsvp',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './rsvp.component.html',
  styleUrl: './rsvp.component.scss'
})
export class RsvpComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PublicApiService);
  private readonly router = inject(Router);

  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);
  readonly successToken = signal<string | null>(null);
  readonly showExtras = signal(false);
  readonly formConfig = signal<RsvpFormConfig>(DEFAULT_FORM);

  readonly form = this.fb.nonNullable.group({
    fullName: ['', Validators.required],
    attending: [true, Validators.required],
    adults: [1, [Validators.required, Validators.min(0)]],
    notes: [''],
    childGuests: this.fb.array<ChildGuestForm>([]),
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

  get dietary() {
    return this.form.controls.dietaryRequirements;
  }

  get transport() {
    return this.form.controls.transport;
  }

  get songs() {
    return this.form.controls.songs;
  }

  get childGuests(): FormArray<ChildGuestForm> {
    return this.form.controls.childGuests;
  }

  ngOnInit(): void {
    this.api.getWedding().subscribe({
      next: (w) => this.formConfig.set(w.rsvpForm ?? DEFAULT_FORM)
    });
  }

  newChildGroup(): ChildGuestForm {
    return this.fb.group({
      fullName: this.fb.nonNullable.control('', Validators.required),
      age: this.fb.control<number | null>(null),
      intolerances: this.fb.nonNullable.control('')
    });
  }

  addChild(): void {
    this.childGuests.push(this.newChildGroup());
  }

  removeChild(index: number): void {
    this.childGuests.removeAt(index);
  }

  hasOptionalBlocks(): boolean {
    const c = this.formConfig();
    return c.showNotes || c.showDietary || c.showTransport || c.showSongs;
  }

  setAttending(value: boolean): void {
    this.form.controls.attending.setValue(value);
    if (!value) {
      this.showExtras.set(false);
      this.form.patchValue({ adults: 0 });
      while (this.childGuests.length) {
        this.childGuests.removeAt(0);
      }
    } else if (this.form.controls.adults.value === 0) {
      this.form.patchValue({ adults: 1 });
    }
  }

  toggleExtras(): void {
    this.showExtras.update((v) => !v);
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

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.submitting.set(true);
    this.error.set(null);

    const cfg = this.formConfig();
    const childrenList = cfg.showChildren
      ? v.childGuests
          .filter((c) => c.fullName.trim())
          .map((c) => ({
            fullName: c.fullName.trim(),
            age: c.age == null || Number.isNaN(Number(c.age)) ? null : Number(c.age),
            intolerances: c.intolerances.trim() || null
          }))
      : [];

    const dietary = cfg.showDietary
      ? v.dietaryRequirements
          .filter((d) => d.type.trim())
          .map((d) => ({ type: d.type.trim(), detail: d.detail.trim() || null }))
      : [];
    const transport = cfg.showTransport
      ? v.transport
          .filter((t) => t.busSlot.trim() || t.stopId.trim() || t.seats > 0)
          .map((t) => ({
            busSlot: t.busSlot.trim() || null,
            stopId: t.stopId.trim() || null,
            seats: t.seats || null
          }))
      : [];
    const songs = cfg.showSongs
      ? v.songs
          .filter((s) => s.title.trim())
          .map((s) => ({ title: s.title.trim(), artist: s.artist.trim() || null }))
      : [];

    this.api
      .submitRsvp({
        inviteCode: null,
        fullName: v.fullName.trim(),
        attending: v.attending,
        adults: v.adults,
        children: childrenList.length,
        notes: cfg.showNotes ? v.notes.trim() || null : null,
        dietaryRequirements: dietary.length ? dietary : null,
        transport: transport.length ? transport : null,
        songs: songs.length ? songs : null,
        childGuests: childrenList.length ? childrenList : null
      })
      .subscribe({
        next: (res) => {
          this.submitting.set(false);
          this.successToken.set(res.editToken);
        },
        error: (err) => {
          this.submitting.set(false);
          const msg = err?.error?.message as string | undefined;
          this.error.set(msg || 'No se pudo enviar la confirmación. Revisad los datos e intentadlo de nuevo.');
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
