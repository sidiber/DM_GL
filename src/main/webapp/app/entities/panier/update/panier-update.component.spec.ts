jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PanierService } from '../service/panier.service';
import { IPanier, Panier } from '../panier.model';
import { ICompte } from 'app/entities/compte/compte.model';
import { CompteService } from 'app/entities/compte/service/compte.service';
import { ISystemePaiement } from 'app/entities/systeme-paiement/systeme-paiement.model';
import { SystemePaiementService } from 'app/entities/systeme-paiement/service/systeme-paiement.service';

import { PanierUpdateComponent } from './panier-update.component';

describe('Component Tests', () => {
  describe('Panier Management Update Component', () => {
    let comp: PanierUpdateComponent;
    let fixture: ComponentFixture<PanierUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let panierService: PanierService;
    let compteService: CompteService;
    let systemePaiementService: SystemePaiementService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PanierUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PanierUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PanierUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      panierService = TestBed.inject(PanierService);
      compteService = TestBed.inject(CompteService);
      systemePaiementService = TestBed.inject(SystemePaiementService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Compte query and add missing value', () => {
        const panier: IPanier = { id: 456 };
        const constitue: ICompte = { id: 72474 };
        panier.constitue = constitue;

        const compteCollection: ICompte[] = [{ id: 6813 }];
        spyOn(compteService, 'query').and.returnValue(of(new HttpResponse({ body: compteCollection })));
        const additionalComptes = [constitue];
        const expectedCollection: ICompte[] = [...additionalComptes, ...compteCollection];
        spyOn(compteService, 'addCompteToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ panier });
        comp.ngOnInit();

        expect(compteService.query).toHaveBeenCalled();
        expect(compteService.addCompteToCollectionIfMissing).toHaveBeenCalledWith(compteCollection, ...additionalComptes);
        expect(comp.comptesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call SystemePaiement query and add missing value', () => {
        const panier: IPanier = { id: 456 };
        const estValidePar: ISystemePaiement = { id: 38354 };
        panier.estValidePar = estValidePar;

        const systemePaiementCollection: ISystemePaiement[] = [{ id: 31046 }];
        spyOn(systemePaiementService, 'query').and.returnValue(of(new HttpResponse({ body: systemePaiementCollection })));
        const additionalSystemePaiements = [estValidePar];
        const expectedCollection: ISystemePaiement[] = [...additionalSystemePaiements, ...systemePaiementCollection];
        spyOn(systemePaiementService, 'addSystemePaiementToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ panier });
        comp.ngOnInit();

        expect(systemePaiementService.query).toHaveBeenCalled();
        expect(systemePaiementService.addSystemePaiementToCollectionIfMissing).toHaveBeenCalledWith(
          systemePaiementCollection,
          ...additionalSystemePaiements
        );
        expect(comp.systemePaiementsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const panier: IPanier = { id: 456 };
        const constitue: ICompte = { id: 55845 };
        panier.constitue = constitue;
        const estValidePar: ISystemePaiement = { id: 27094 };
        panier.estValidePar = estValidePar;

        activatedRoute.data = of({ panier });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(panier));
        expect(comp.comptesSharedCollection).toContain(constitue);
        expect(comp.systemePaiementsSharedCollection).toContain(estValidePar);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const panier = { id: 123 };
        spyOn(panierService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ panier });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: panier }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(panierService.update).toHaveBeenCalledWith(panier);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const panier = new Panier();
        spyOn(panierService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ panier });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: panier }));
        saveSubject.complete();

        // THEN
        expect(panierService.create).toHaveBeenCalledWith(panier);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const panier = { id: 123 };
        spyOn(panierService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ panier });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(panierService.update).toHaveBeenCalledWith(panier);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCompteById', () => {
        it('Should return tracked Compte primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCompteById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackSystemePaiementById', () => {
        it('Should return tracked SystemePaiement primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSystemePaiementById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
