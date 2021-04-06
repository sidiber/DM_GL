import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICourse, Course } from '../course.model';
import { CourseService } from '../service/course.service';
import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { IPlat } from 'app/entities/plat/plat.model';
import { PlatService } from 'app/entities/plat/service/plat.service';
import { ICompte } from 'app/entities/compte/compte.model';
import { CompteService } from 'app/entities/compte/service/compte.service';

@Component({
  selector: 'jhi-course-update',
  templateUrl: './course-update.component.html',
})
export class CourseUpdateComponent implements OnInit {
  isSaving = false;

  montantsCollection: IPanier[] = [];
  platsSharedCollection: IPlat[] = [];
  comptesSharedCollection: ICompte[] = [];

  editForm = this.fb.group({
    id: [],
    createdAt: [null, [Validators.required]],
    etat: [],
    startTime: [null, [Validators.required]],
    endTime: [],
    montant: [],
    plats: [],
    estlivre: [],
  });

  constructor(
    protected courseService: CourseService,
    protected panierService: PanierService,
    protected platService: PlatService,
    protected compteService: CompteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ course }) => {
      if (course.id === undefined) {
        const today = dayjs().startOf('day');
        course.createdAt = today;
        course.startTime = today;
        course.endTime = today;
      }

      this.updateForm(course);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const course = this.createFromForm();
    if (course.id !== undefined) {
      this.subscribeToSaveResponse(this.courseService.update(course));
    } else {
      this.subscribeToSaveResponse(this.courseService.create(course));
    }
  }

  trackPanierById(index: number, item: IPanier): number {
    return item.id!;
  }

  trackPlatById(index: number, item: IPlat): number {
    return item.id!;
  }

  trackCompteById(index: number, item: ICompte): number {
    return item.id!;
  }

  getSelectedPlat(option: IPlat, selectedVals?: IPlat[]): IPlat {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICourse>>): void {
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

  protected updateForm(course: ICourse): void {
    this.editForm.patchValue({
      id: course.id,
      createdAt: course.createdAt ? course.createdAt.format(DATE_TIME_FORMAT) : null,
      etat: course.etat,
      startTime: course.startTime ? course.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: course.endTime ? course.endTime.format(DATE_TIME_FORMAT) : null,
      montant: course.montant,
      plats: course.plats,
      estlivre: course.estlivre,
    });

    this.montantsCollection = this.panierService.addPanierToCollectionIfMissing(this.montantsCollection, course.montant);
    this.platsSharedCollection = this.platService.addPlatToCollectionIfMissing(this.platsSharedCollection, ...(course.plats ?? []));
    this.comptesSharedCollection = this.compteService.addCompteToCollectionIfMissing(this.comptesSharedCollection, course.estlivre);
  }

  protected loadRelationshipsOptions(): void {
    this.panierService
      .query({ 'courseId.specified': 'false' })
      .pipe(map((res: HttpResponse<IPanier[]>) => res.body ?? []))
      .pipe(map((paniers: IPanier[]) => this.panierService.addPanierToCollectionIfMissing(paniers, this.editForm.get('montant')!.value)))
      .subscribe((paniers: IPanier[]) => (this.montantsCollection = paniers));

    this.platService
      .query()
      .pipe(map((res: HttpResponse<IPlat[]>) => res.body ?? []))
      .pipe(map((plats: IPlat[]) => this.platService.addPlatToCollectionIfMissing(plats, ...(this.editForm.get('plats')!.value ?? []))))
      .subscribe((plats: IPlat[]) => (this.platsSharedCollection = plats));

    this.compteService
      .query()
      .pipe(map((res: HttpResponse<ICompte[]>) => res.body ?? []))
      .pipe(map((comptes: ICompte[]) => this.compteService.addCompteToCollectionIfMissing(comptes, this.editForm.get('estlivre')!.value)))
      .subscribe((comptes: ICompte[]) => (this.comptesSharedCollection = comptes));
  }

  protected createFromForm(): ICourse {
    return {
      ...new Course(),
      id: this.editForm.get(['id'])!.value,
      createdAt: this.editForm.get(['createdAt'])!.value ? dayjs(this.editForm.get(['createdAt'])!.value, DATE_TIME_FORMAT) : undefined,
      etat: this.editForm.get(['etat'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? dayjs(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? dayjs(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      montant: this.editForm.get(['montant'])!.value,
      plats: this.editForm.get(['plats'])!.value,
      estlivre: this.editForm.get(['estlivre'])!.value,
    };
  }
}
