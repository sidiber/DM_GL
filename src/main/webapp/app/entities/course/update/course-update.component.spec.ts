jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CourseService } from '../service/course.service';
import { ICourse, Course } from '../course.model';
import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { IPlat } from 'app/entities/plat/plat.model';
import { PlatService } from 'app/entities/plat/service/plat.service';
import { ICompte } from 'app/entities/compte/compte.model';
import { CompteService } from 'app/entities/compte/service/compte.service';

import { CourseUpdateComponent } from './course-update.component';

describe('Component Tests', () => {
  describe('Course Management Update Component', () => {
    let comp: CourseUpdateComponent;
    let fixture: ComponentFixture<CourseUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let courseService: CourseService;
    let panierService: PanierService;
    let platService: PlatService;
    let compteService: CompteService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CourseUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CourseUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CourseUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      courseService = TestBed.inject(CourseService);
      panierService = TestBed.inject(PanierService);
      platService = TestBed.inject(PlatService);
      compteService = TestBed.inject(CompteService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call montant query and add missing value', () => {
        const course: ICourse = { id: 456 };
        const montant: IPanier = { id: 23537 };
        course.montant = montant;

        const montantCollection: IPanier[] = [{ id: 7790 }];
        spyOn(panierService, 'query').and.returnValue(of(new HttpResponse({ body: montantCollection })));
        const expectedCollection: IPanier[] = [montant, ...montantCollection];
        spyOn(panierService, 'addPanierToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ course });
        comp.ngOnInit();

        expect(panierService.query).toHaveBeenCalled();
        expect(panierService.addPanierToCollectionIfMissing).toHaveBeenCalledWith(montantCollection, montant);
        expect(comp.montantsCollection).toEqual(expectedCollection);
      });

      it('Should call Plat query and add missing value', () => {
        const course: ICourse = { id: 456 };
        const plats: IPlat[] = [{ id: 30505 }];
        course.plats = plats;

        const platCollection: IPlat[] = [{ id: 89065 }];
        spyOn(platService, 'query').and.returnValue(of(new HttpResponse({ body: platCollection })));
        const additionalPlats = [...plats];
        const expectedCollection: IPlat[] = [...additionalPlats, ...platCollection];
        spyOn(platService, 'addPlatToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ course });
        comp.ngOnInit();

        expect(platService.query).toHaveBeenCalled();
        expect(platService.addPlatToCollectionIfMissing).toHaveBeenCalledWith(platCollection, ...additionalPlats);
        expect(comp.platsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Compte query and add missing value', () => {
        const course: ICourse = { id: 456 };
        const estlivre: ICompte = { id: 20506 };
        course.estlivre = estlivre;

        const compteCollection: ICompte[] = [{ id: 91775 }];
        spyOn(compteService, 'query').and.returnValue(of(new HttpResponse({ body: compteCollection })));
        const additionalComptes = [estlivre];
        const expectedCollection: ICompte[] = [...additionalComptes, ...compteCollection];
        spyOn(compteService, 'addCompteToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ course });
        comp.ngOnInit();

        expect(compteService.query).toHaveBeenCalled();
        expect(compteService.addCompteToCollectionIfMissing).toHaveBeenCalledWith(compteCollection, ...additionalComptes);
        expect(comp.comptesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const course: ICourse = { id: 456 };
        const montant: IPanier = { id: 14913 };
        course.montant = montant;
        const plats: IPlat = { id: 24816 };
        course.plats = [plats];
        const estlivre: ICompte = { id: 13630 };
        course.estlivre = estlivre;

        activatedRoute.data = of({ course });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(course));
        expect(comp.montantsCollection).toContain(montant);
        expect(comp.platsSharedCollection).toContain(plats);
        expect(comp.comptesSharedCollection).toContain(estlivre);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const course = { id: 123 };
        spyOn(courseService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ course });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: course }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(courseService.update).toHaveBeenCalledWith(course);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const course = new Course();
        spyOn(courseService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ course });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: course }));
        saveSubject.complete();

        // THEN
        expect(courseService.create).toHaveBeenCalledWith(course);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const course = { id: 123 };
        spyOn(courseService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ course });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(courseService.update).toHaveBeenCalledWith(course);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackPanierById', () => {
        it('Should return tracked Panier primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPanierById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackPlatById', () => {
        it('Should return tracked Plat primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPlatById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackCompteById', () => {
        it('Should return tracked Compte primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCompteById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedPlat', () => {
        it('Should return option if no Plat is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedPlat(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Plat for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedPlat(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Plat is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedPlat(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
