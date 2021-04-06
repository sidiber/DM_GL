import { ICompte } from 'app/entities/compte/compte.model';

export interface ICooperative {
  id?: number;
  nomCoop?: string | null;
  villeCoop?: string | null;
  comptes?: ICompte[] | null;
}

export class Cooperative implements ICooperative {
  constructor(public id?: number, public nomCoop?: string | null, public villeCoop?: string | null, public comptes?: ICompte[] | null) {}
}

export function getCooperativeIdentifier(cooperative: ICooperative): number | undefined {
  return cooperative.id;
}
