jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PlatService } from '../service/plat.service';
import { IPlat, Plat } from '../plat.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';

import { PlatUpdateComponent } from './plat-update.component';

describe('Component Tests', () => {
  describe('Plat Management Update Component', () => {
    let comp: PlatUpdateComponent;
    let fixture: ComponentFixture<PlatUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let platService: PlatService;
    let restaurantService: RestaurantService;
    let panierService: PanierService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PlatUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PlatUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PlatUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      platService = TestBed.inject(PlatService);
      restaurantService = TestBed.inject(RestaurantService);
      panierService = TestBed.inject(PanierService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Restaurant query and add missing value', () => {
        const plat: IPlat = { id: 456 };
        const restaurants: IRestaurant[] = [{ id: 32027 }];
        plat.restaurants = restaurants;

        const restaurantCollection: IRestaurant[] = [{ id: 27147 }];
        spyOn(restaurantService, 'query').and.returnValue(of(new HttpResponse({ body: restaurantCollection })));
        const additionalRestaurants = [...restaurants];
        const expectedCollection: IRestaurant[] = [...additionalRestaurants, ...restaurantCollection];
        spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        expect(restaurantService.query).toHaveBeenCalled();
        expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(restaurantCollection, ...additionalRestaurants);
        expect(comp.restaurantsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Panier query and add missing value', () => {
        const plat: IPlat = { id: 456 };
        const paniers: IPanier[] = [{ id: 40132 }];
        plat.paniers = paniers;

        const panierCollection: IPanier[] = [{ id: 84521 }];
        spyOn(panierService, 'query').and.returnValue(of(new HttpResponse({ body: panierCollection })));
        const additionalPaniers = [...paniers];
        const expectedCollection: IPanier[] = [...additionalPaniers, ...panierCollection];
        spyOn(panierService, 'addPanierToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        expect(panierService.query).toHaveBeenCalled();
        expect(panierService.addPanierToCollectionIfMissing).toHaveBeenCalledWith(panierCollection, ...additionalPaniers);
        expect(comp.paniersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const plat: IPlat = { id: 456 };
        const restaurants: IRestaurant = { id: 77783 };
        plat.restaurants = [restaurants];
        const paniers: IPanier = { id: 72716 };
        plat.paniers = [paniers];

        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(plat));
        expect(comp.restaurantsSharedCollection).toContain(restaurants);
        expect(comp.paniersSharedCollection).toContain(paniers);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const plat = { id: 123 };
        spyOn(platService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: plat }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(platService.update).toHaveBeenCalledWith(plat);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const plat = new Plat();
        spyOn(platService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: plat }));
        saveSubject.complete();

        // THEN
        expect(platService.create).toHaveBeenCalledWith(plat);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const plat = { id: 123 };
        spyOn(platService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(platService.update).toHaveBeenCalledWith(plat);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackRestaurantById', () => {
        it('Should return tracked Restaurant primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackRestaurantById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackPanierById', () => {
        it('Should return tracked Panier primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPanierById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedRestaurant', () => {
        it('Should return option if no Restaurant is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedRestaurant(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Restaurant for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedRestaurant(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Restaurant is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedRestaurant(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });

      describe('getSelectedPanier', () => {
        it('Should return option if no Panier is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedPanier(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Panier for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedPanier(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Panier is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedPanier(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
