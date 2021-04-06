import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlat } from '../plat.model';
import { PlatService } from '../service/plat.service';
import { PlatDeleteDialogComponent } from '../delete/plat-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-plat',
  templateUrl: './plat.component.html',
})
export class PlatComponent implements OnInit {
  plats?: IPlat[];
  isLoading = false;

  constructor(protected platService: PlatService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.platService.query().subscribe(
      (res: HttpResponse<IPlat[]>) => {
        this.isLoading = false;
        this.plats = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IPlat): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(plat: IPlat): void {
    const modalRef = this.modalService.open(PlatDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.plat = plat;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
