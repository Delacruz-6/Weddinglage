import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

/** Legacy /upload?t=… links redirect to the native-feeling /fotos app. */
@Component({
  selector: 'app-upload',
  standalone: true,
  template: ''
})
export class UploadComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  ngOnInit(): void {
    const t = this.route.snapshot.queryParamMap.get('t');
    void this.router.navigate(['/fotos'], {
      queryParams: t ? { t } : {},
      replaceUrl: true
    });
  }
}
