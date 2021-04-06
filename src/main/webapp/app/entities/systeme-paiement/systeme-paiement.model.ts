import * as dayjs from 'dayjs';
import { IPanier } from 'app/entities/panier/panier.model';

export interface ISystemePaiement {
  id?: number;
  numCarte?: string | null;
  dateExpiration?: dayjs.Dayjs;
  typeCarte?: string | null;
  montant?: string | null;
  dateFacture?: dayjs.Dayjs;
  paniers?: IPanier[] | null;
}

export class SystemePaiement implements ISystemePaiement {
  constructor(
    public id?: number,
    public numCarte?: string | null,
    public dateExpiration?: dayjs.Dayjs,
    public typeCarte?: string | null,
    public montant?: string | null,
    public dateFacture?: dayjs.Dayjs,
    public paniers?: IPanier[] | null
  ) {}
}

export function getSystemePaiementIdentifier(systemePaiement: ISystemePaiement): number | undefined {
  return systemePaiement.id;
}
