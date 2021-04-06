import { IPanier } from 'app/entities/panier/panier.model';
import { ICourse } from 'app/entities/course/course.model';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';

export interface ICompte {
  id?: number;
  nom?: string;
  prenom?: string;
  email?: string;
  phoneNumber?: string | null;
  addressCompte?: string | null;
  codePCompte?: string | null;
  villeCompte?: string | null;
  paniers?: IPanier[] | null;
  courses?: ICourse[] | null;
  membre?: ICooperative | null;
}

export class Compte implements ICompte {
  constructor(
    public id?: number,
    public nom?: string,
    public prenom?: string,
    public email?: string,
    public phoneNumber?: string | null,
    public addressCompte?: string | null,
    public codePCompte?: string | null,
    public villeCompte?: string | null,
    public paniers?: IPanier[] | null,
    public courses?: ICourse[] | null,
    public membre?: ICooperative | null
  ) {}
}

export function getCompteIdentifier(compte: ICompte): number | undefined {
  return compte.id;
}
