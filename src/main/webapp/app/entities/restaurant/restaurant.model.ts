import { IPlat } from 'app/entities/plat/plat.model';

export interface IRestaurant {
  id?: number;
  nomResto?: string | null;
  fraisLivraison?: string | null;
  adresseResto?: string | null;
  codePResto?: string | null;
  villeResto?: string | null;
  plats?: IPlat[] | null;
}

export class Restaurant implements IRestaurant {
  constructor(
    public id?: number,
    public nomResto?: string | null,
    public fraisLivraison?: string | null,
    public adresseResto?: string | null,
    public codePResto?: string | null,
    public villeResto?: string | null,
    public plats?: IPlat[] | null
  ) {}
}

export function getRestaurantIdentifier(restaurant: IRestaurant): number | undefined {
  return restaurant.id;
}
