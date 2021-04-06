import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { EtatCourse } from 'app/entities/enumerations/etat-course.model';
import { ICourse, Course } from '../course.model';

import { CourseService } from './course.service';

describe('Service Tests', () => {
  describe('Course Service', () => {
    let service: CourseService;
    let httpMock: HttpTestingController;
    let elemDefault: ICourse;
    let expectedResult: ICourse | ICourse[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CourseService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        createdAt: currentDate,
        etat: EtatCourse.CREE,
        startTime: currentDate,
        endTime: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            startTime: currentDate.format(DATE_TIME_FORMAT),
            endTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Course', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            startTime: currentDate.format(DATE_TIME_FORMAT),
            endTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
            startTime: currentDate,
            endTime: currentDate,
          },
          returnedFromService
        );

        service.create(new Course()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Course', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            etat: 'BBBBBB',
            startTime: currentDate.format(DATE_TIME_FORMAT),
            endTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
            startTime: currentDate,
            endTime: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Course', () => {
        const patchObject = Object.assign(
          {
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            etat: 'BBBBBB',
            startTime: currentDate.format(DATE_TIME_FORMAT),
            endTime: currentDate.format(DATE_TIME_FORMAT),
          },
          new Course()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            createdAt: currentDate,
            startTime: currentDate,
            endTime: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Course', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            etat: 'BBBBBB',
            startTime: currentDate.format(DATE_TIME_FORMAT),
            endTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
            startTime: currentDate,
            endTime: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Course', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCourseToCollectionIfMissing', () => {
        it('should add a Course to an empty array', () => {
          const course: ICourse = { id: 123 };
          expectedResult = service.addCourseToCollectionIfMissing([], course);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(course);
        });

        it('should not add a Course to an array that contains it', () => {
          const course: ICourse = { id: 123 };
          const courseCollection: ICourse[] = [
            {
              ...course,
            },
            { id: 456 },
          ];
          expectedResult = service.addCourseToCollectionIfMissing(courseCollection, course);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Course to an array that doesn't contain it", () => {
          const course: ICourse = { id: 123 };
          const courseCollection: ICourse[] = [{ id: 456 }];
          expectedResult = service.addCourseToCollectionIfMissing(courseCollection, course);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(course);
        });

        it('should add only unique Course to an array', () => {
          const courseArray: ICourse[] = [{ id: 123 }, { id: 456 }, { id: 53688 }];
          const courseCollection: ICourse[] = [{ id: 123 }];
          expectedResult = service.addCourseToCollectionIfMissing(courseCollection, ...courseArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const course: ICourse = { id: 123 };
          const course2: ICourse = { id: 456 };
          expectedResult = service.addCourseToCollectionIfMissing([], course, course2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(course);
          expect(expectedResult).toContain(course2);
        });

        it('should accept null and undefined values', () => {
          const course: ICourse = { id: 123 };
          expectedResult = service.addCourseToCollectionIfMissing([], null, course, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(course);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
