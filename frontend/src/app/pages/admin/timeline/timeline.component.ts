import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminApiService } from '../../../core/services/admin-api.service';
import { TimelineEvent } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-timeline',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './timeline.component.html',
  styleUrl: './timeline.component.scss'
})
export class TimelineComponent implements OnInit {
  private readonly api = inject(AdminApiService);
  private readonly fb = inject(FormBuilder);

  readonly items = signal<TimelineEvent[]>([]);
  readonly editingId = signal<number | null>(null);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    eventTime: [''],
    title: ['', Validators.required],
    description: [''],
    icon: [''],
    sortOrder: [0]
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.listTimeline().subscribe({
      next: (list) => this.items.set(list),
      error: () => this.error.set('No se pudo cargar el programa.')
    });
  }

  startEdit(item: TimelineEvent): void {
    this.editingId.set(item.id);
    this.form.patchValue({
      eventTime: item.eventTime ? item.eventTime.slice(0, 5) : '',
      title: item.title ?? '',
      description: item.description ?? '',
      icon: item.icon ?? '',
      sortOrder: item.sortOrder
    });
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({ eventTime: '', title: '', description: '', icon: '', sortOrder: 0 });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const body: TimelineEvent = {
      id: this.editingId(),
      eventTime: v.eventTime ? `${v.eventTime}:00` : null,
      title: v.title.trim(),
      description: v.description.trim() || null,
      icon: v.icon.trim() || null,
      sortOrder: v.sortOrder
    };
    const id = this.editingId();
    const req = id == null ? this.api.createTimeline(body) : this.api.updateTimeline(id, body);
    req.subscribe({
      next: () => {
        this.cancelEdit();
        this.reload();
      },
      error: () => this.error.set('No se pudo guardar.')
    });
  }

  remove(id: number): void {
    if (!confirm('¿Eliminar este evento?')) {
      return;
    }
    this.api.deleteTimeline(id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('No se pudo eliminar.')
    });
  }
}
