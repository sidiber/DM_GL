import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { IPanier } from 'app/entities/panier/panier.model';
import { ICourse } from 'app/entities/course/course.model';

export interface IPlat {
  id?: number;
  nomPlat?: string;
  description?: string | null;
  prix?: string;
  photoContentType?: string | null;
  photo?: string | null;
  restaurants?: IRestaurant[] | null;
  paniers?: IPanier[] | null;
  courses?: ICourse[] | null;
}

export class Plat implements IPlat {
  constructor(
    public id?: number,
    public nomPlat?: string,
    public description?: string | null,
    public prix?: string,
    public photoContentType?: string | null,
    public photo?: string | null,
    public restaurants?: IRestaurant[] | null,
    public paniers?: IPanier[] | null,
    public courses?: ICourse[] | null
  ) {}
}

export function getPlatIdentifier(plat: IPlat): number | undefined {
  return plat.id;
}
