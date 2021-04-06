import { ICompte } from 'app/entities/compte/compte.model';
import { ISystemePaiement } from 'app/entities/systeme-paiement/systeme-paiement.model';
import { IPlat } from 'app/entities/plat/plat.model';

export interface IPanier {
  id?: number;
  prixTotal?: string | null;
  constitue?: ICompte | null;
  estValidePar?: ISystemePaiement | null;
  plats?: IPlat[] | null;
}

export class Panier implements IPanier {
  constructor(
    public id?: number,
    public prixTotal?: string | null,
    public constitue?: ICompte | null,
    public estValidePar?: ISystemePaiement | null,
    public plats?: IPlat[] | null
  ) {}
}

export function getPanierIdentifier(panier: IPanier): number | undefined {
  return panier.id;
}
