import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ISystemePaiement, SystemePaiement } from '../systeme-paiement.model';
import { SystemePaiementService } from '../service/systeme-paiement.service';

@Component({
  selector: 'jhi-systeme-paiement-update',
  templateUrl: './systeme-paiement-update.component.html',
})
export class SystemePaiementUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    numCarte: [null, [Validators.minLength(16), Validators.maxLength(16), Validators.pattern('^[0-9_ ]$')]],
    dateExpiration: [null, [Validators.required]],
    typeCarte: [null, [Validators.maxLength(10), Validators.pattern('^[a-zA-Z_ ]$')]],
    montant: [],
    dateFacture: [null, [Validators.required]],
  });

  constructor(
    protected systemePaiementService: SystemePaiementService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemePaiement }) => {
      if (systemePaiement.id === undefined) {
        const today = dayjs().startOf('day');
        systemePaiement.dateExpiration = today;
        systemePaiement.dateFacture = today;
      }

      this.updateForm(systemePaiement);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const systemePaiement = this.createFromForm();
    if (systemePaiement.id !== undefined) {
      this.subscribeToSaveResponse(this.systemePaiementService.update(systemePaiement));
    } else {
      this.subscribeToSaveResponse(this.systemePaiementService.create(systemePaiement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISystemePaiement>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(systemePaiement: ISystemePaiement): void {
    this.editForm.patchValue({
      id: systemePaiement.id,
      numCarte: systemePaiement.numCarte,
      dateExpiration: systemePaiement.dateExpiration ? systemePaiement.dateExpiration.format(DATE_TIME_FORMAT) : null,
      typeCarte: systemePaiement.typeCarte,
      montant: systemePaiement.montant,
      dateFacture: systemePaiement.dateFacture ? systemePaiement.dateFacture.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): ISystemePaiement {
    return {
      ...new SystemePaiement(),
      id: this.editForm.get(['id'])!.value,
      numCarte: this.editForm.get(['numCarte'])!.value,
      dateExpiration: this.editForm.get(['dateExpiration'])!.value
        ? dayjs(this.editForm.get(['dateExpiration'])!.value, DATE_TIME_FORMAT)
        : undefined,
      typeCarte: this.editForm.get(['typeCarte'])!.value,
      montant: this.editForm.get(['montant'])!.value,
      dateFacture: this.editForm.get(['dateFacture'])!.value
        ? dayjs(this.editForm.get(['dateFacture'])!.value, DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
