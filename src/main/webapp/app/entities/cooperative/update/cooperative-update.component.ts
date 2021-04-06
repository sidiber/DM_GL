import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICooperative, Cooperative } from '../cooperative.model';
import { CooperativeService } from '../service/cooperative.service';

@Component({
  selector: 'jhi-cooperative-update',
  templateUrl: './cooperative-update.component.html',
})
export class CooperativeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nomCoop: [],
    villeCoop: [],
  });

  constructor(protected cooperativeService: CooperativeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cooperative }) => {
      this.updateForm(cooperative);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cooperative = this.createFromForm();
    if (cooperative.id !== undefined) {
      this.subscribeToSaveResponse(this.cooperativeService.update(cooperative));
    } else {
      this.subscribeToSaveResponse(this.cooperativeService.create(cooperative));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICooperative>>): void {
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

  protected updateForm(cooperative: ICooperative): void {
    this.editForm.patchValue({
      id: cooperative.id,
      nomCoop: cooperative.nomCoop,
      villeCoop: cooperative.villeCoop,
    });
  }

  protected createFromForm(): ICooperative {
    return {
      ...new Cooperative(),
      id: this.editForm.get(['id'])!.value,
      nomCoop: this.editForm.get(['nomCoop'])!.value,
      villeCoop: this.editForm.get(['villeCoop'])!.value,
    };
  }
}
