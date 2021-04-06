import * as dayjs from 'dayjs';
import { IPanier } from 'app/entities/panier/panier.model';
import { IPlat } from 'app/entities/plat/plat.model';
import { ICompte } from 'app/entities/compte/compte.model';
import { EtatCourse } from 'app/entities/enumerations/etat-course.model';

export interface ICourse {
  id?: number;
  createdAt?: dayjs.Dayjs;
  etat?: EtatCourse | null;
  startTime?: dayjs.Dayjs;
  endTime?: dayjs.Dayjs | null;
  montant?: IPanier | null;
  plats?: IPlat[] | null;
  livre?: ICompte | null;
}

export class Course implements ICourse {
  constructor(
    public id?: number,
    public createdAt?: dayjs.Dayjs,
    public etat?: EtatCourse | null,
    public startTime?: dayjs.Dayjs,
    public endTime?: dayjs.Dayjs | null,
    public montant?: IPanier | null,
    public plats?: IPlat[] | null,
    public livre?: ICompte | null
  ) {}
}

export function getCourseIdentifier(course: ICourse): number | undefined {
  return course.id;
}
