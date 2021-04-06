import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PlatService } from '../service/plat.service';

import { PlatComponent } from './plat.component';

describe('Component Tests', () => {
  describe('Plat Management Component', () => {
    let comp: PlatComponent;
    let fixture: ComponentFixture<PlatComponent>;
    let service: PlatService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PlatComponent],
      })
        .overrideTemplate(PlatComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PlatComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(PlatService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.plats?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
