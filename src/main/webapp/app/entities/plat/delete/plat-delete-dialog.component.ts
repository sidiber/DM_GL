import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlat } from '../plat.model';
import { PlatService } from '../service/plat.service';

@Component({
  templateUrl: './plat-delete-dialog.component.html',
})
export class PlatDeleteDialogComponent {
  plat?: IPlat;

  constructor(protected platService: PlatService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.platService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
