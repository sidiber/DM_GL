package fr.polytech.info4.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link fr.polytech.info4.domain.SystemePaiement} entity. This class is used
 * in {@link fr.polytech.info4.web.rest.SystemePaiementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /systeme-paiements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SystemePaiementCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter numCarte;

    private InstantFilter dateExpiration;

    private StringFilter typeCarte;

    private StringFilter montant;

    private InstantFilter dateFacture;

    private LongFilter panierId;

    public SystemePaiementCriteria() {}

    public SystemePaiementCriteria(SystemePaiementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.numCarte = other.numCarte == null ? null : other.numCarte.copy();
        this.dateExpiration = other.dateExpiration == null ? null : other.dateExpiration.copy();
        this.typeCarte = other.typeCarte == null ? null : other.typeCarte.copy();
        this.montant = other.montant == null ? null : other.montant.copy();
        this.dateFacture = other.dateFacture == null ? null : other.dateFacture.copy();
        this.panierId = other.panierId == null ? null : other.panierId.copy();
    }

    @Override
    public SystemePaiementCriteria copy() {
        return new SystemePaiementCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNumCarte() {
        return numCarte;
    }

    public StringFilter numCarte() {
        if (numCarte == null) {
            numCarte = new StringFilter();
        }
        return numCarte;
    }

    public void setNumCarte(StringFilter numCarte) {
        this.numCarte = numCarte;
    }

    public InstantFilter getDateExpiration() {
        return dateExpiration;
    }

    public InstantFilter dateExpiration() {
        if (dateExpiration == null) {
            dateExpiration = new InstantFilter();
        }
        return dateExpiration;
    }

    public void setDateExpiration(InstantFilter dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public StringFilter getTypeCarte() {
        return typeCarte;
    }

    public StringFilter typeCarte() {
        if (typeCarte == null) {
            typeCarte = new StringFilter();
        }
        return typeCarte;
    }

    public void setTypeCarte(StringFilter typeCarte) {
        this.typeCarte = typeCarte;
    }

    public StringFilter getMontant() {
        return montant;
    }

    public StringFilter montant() {
        if (montant == null) {
            montant = new StringFilter();
        }
        return montant;
    }

    public void setMontant(StringFilter montant) {
        this.montant = montant;
    }

    public InstantFilter getDateFacture() {
        return dateFacture;
    }

    public InstantFilter dateFacture() {
        if (dateFacture == null) {
            dateFacture = new InstantFilter();
        }
        return dateFacture;
    }

    public void setDateFacture(InstantFilter dateFacture) {
        this.dateFacture = dateFacture;
    }

    public LongFilter getPanierId() {
        return panierId;
    }

    public LongFilter panierId() {
        if (panierId == null) {
            panierId = new LongFilter();
        }
        return panierId;
    }

    public void setPanierId(LongFilter panierId) {
        this.panierId = panierId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SystemePaiementCriteria that = (SystemePaiementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numCarte, that.numCarte) &&
            Objects.equals(dateExpiration, that.dateExpiration) &&
            Objects.equals(typeCarte, that.typeCarte) &&
            Objects.equals(montant, that.montant) &&
            Objects.equals(dateFacture, that.dateFacture) &&
            Objects.equals(panierId, that.panierId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numCarte, dateExpiration, typeCarte, montant, dateFacture, panierId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemePaiementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (numCarte != null ? "numCarte=" + numCarte + ", " : "") +
            (dateExpiration != null ? "dateExpiration=" + dateExpiration + ", " : "") +
            (typeCarte != null ? "typeCarte=" + typeCarte + ", " : "") +
            (montant != null ? "montant=" + montant + ", " : "") +
            (dateFacture != null ? "dateFacture=" + dateFacture + ", " : "") +
            (panierId != null ? "panierId=" + panierId + ", " : "") +
            "}";
    }
}
