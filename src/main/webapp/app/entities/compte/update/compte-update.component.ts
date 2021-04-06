import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICompte, Compte } from '../compte.model';
import { CompteService } from '../service/compte.service';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';
import { CooperativeService } from 'app/entities/cooperative/service/cooperative.service';

@Component({
  selector: 'jhi-compte-update',
  templateUrl: './compte-update.component.html',
})
export class CompteUpdateComponent implements OnInit {
  isSaving = false;

  cooperativesSharedCollection: ICooperative[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required, Validators.pattern('^[a-zA-Z_ ]+$')]],
    prenom: [null, [Validators.required, Validators.pattern('^[a-zA-Z_ ]+$')]],
    email: [
      null,
      [
        Validators.required,
        Validators.pattern('^([a-zA-Z0-9_\\-\\.]+)@(|hotmail|yahoo|imag|gmail|etu.univ-grenoble-alpes|univ-grenoble-alpes+)\\.(fr|com)$'),
      ],
    ],
    phoneNumber: [null, [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('^[0-9]+$')]],
    addressCompte: [null, [Validators.pattern('^[a-zA-Z0-9_ ]+$')]],
    codePCompte: [null, [Validators.minLength(5), Validators.maxLength(5), Validators.pattern('^[0-9]+$')]],
    villeCompte: [null, [Validators.pattern('^[a-zA-Z0-9_ ]+$')]],
    membre: [],
  });

  constructor(
    protected compteService: CompteService,
    protected cooperativeService: CooperativeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ compte }) => {
      this.updateForm(compte);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const compte = this.createFromForm();
    if (compte.id !== undefined) {
      this.subscribeToSaveResponse(this.compteService.update(compte));
    } else {
      this.subscribeToSaveResponse(this.compteService.create(compte));
    }
  }

  trackCooperativeById(index: number, item: ICooperative): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompte>>): void {
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

  protected updateForm(compte: ICompte): void {
    this.editForm.patchValue({
      id: compte.id,
      nom: compte.nom,
      prenom: compte.prenom,
      email: compte.email,
      phoneNumber: compte.phoneNumber,
      addressCompte: compte.addressCompte,
      codePCompte: compte.codePCompte,
      villeCompte: compte.villeCompte,
      membre: compte.membre,
    });

    this.cooperativesSharedCollection = this.cooperativeService.addCooperativeToCollectionIfMissing(
      this.cooperativesSharedCollection,
      compte.membre
    );
  }

  protected loadRelationshipsOptions(): void {
    this.cooperativeService
      .query()
      .pipe(map((res: HttpResponse<ICooperative[]>) => res.body ?? []))
      .pipe(
        map((cooperatives: ICooperative[]) =>
          this.cooperativeService.addCooperativeToCollectionIfMissing(cooperatives, this.editForm.get('membre')!.value)
        )
      )
      .subscribe((cooperatives: ICooperative[]) => (this.cooperativesSharedCollection = cooperatives));
  }

  protected createFromForm(): ICompte {
    return {
      ...new Compte(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      prenom: this.editForm.get(['prenom'])!.value,
      email: this.editForm.get(['email'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      addressCompte: this.editForm.get(['addressCompte'])!.value,
      codePCompte: this.editForm.get(['codePCompte'])!.value,
      villeCompte: this.editForm.get(['villeCompte'])!.value,
      membre: this.editForm.get(['membre'])!.value,
    };
  }
}
