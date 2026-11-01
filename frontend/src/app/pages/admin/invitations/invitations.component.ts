import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { Invitation } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-invitations',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './invitations.component.html',
  styleUrl: './invitations.component.scss'
})
export class InvitationsComponent implements OnInit {
  private readonly api = inject(AdminApiService);
  private readonly fb = inject(FormBuilder);

  readonly items = signal<Invitation[]>([]);
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly editingId = signal<number | null>(null);

  readonly form = this.fb.nonNullable.group({
    inviteCode: ['', Validators.required],
    label: [''],
    maxAttendees: [2, [Validators.required, Validators.min(1)]],
    active: [true]
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.listInvitations().subscribe({
      next: (list) => this.items.set(list),
      error: () => this.error.set('No se pudieron cargar las invitaciones.')
    });
  }

  startEdit(item: Invitation): void {
    this.editingId.set(item.id);
    this.form.patchValue({
      inviteCode: item.inviteCode,
      label: item.label ?? '',
      maxAttendees: item.maxAttendees,
      active: item.active
    });
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({ inviteCode: '', label: '', maxAttendees: 2, active: true });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const body = {
      inviteCode: v.inviteCode.trim(),
      label: v.label.trim() || null,
      maxAttendees: v.maxAttendees,
      active: v.active
    };
    const id = this.editingId();
    const req = id == null ? this.api.createInvitation(body) : this.api.updateInvitation(id, body);
    req.subscribe({
      next: () => {
        this.message.set('Guardado.');
        this.cancelEdit();
        this.reload();
      },
      error: () => this.error.set('No se pudo guardar la invitación.')
    });
  }

  remove(id: number): void {
    if (!confirm('¿Eliminar esta invitación?')) {
      return;
    }
    this.api.deleteInvitation(id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('No se pudo eliminar.')
    });
  }

  assignOrnament(id: number): void {
    this.api.assignOrnament(id, { ornamentCode: null, random: true }).subscribe({
      next: (o) => {
        this.message.set(`Adorno: ${o.ornamentName || o.ornamentCode}`);
        this.reload();
      },
      error: () => this.error.set('No se pudo asignar adorno.')
    });
  }

  regenerate(id: number): void {
    this.api.regenerateToken(id).subscribe({
      next: (res) => {
        this.message.set(`Nuevo editToken: ${res.editToken}`);
      },
      error: () => this.error.set('No se pudo regenerar el token.')
    });
  }
}
