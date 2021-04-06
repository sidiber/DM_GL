package fr.polytech.info4.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link fr.polytech.info4.domain.Panier} entity. This class is used
 * in {@link fr.polytech.info4.web.rest.PanierResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /paniers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PanierCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter prixTotal;

    private LongFilter constitueId;

    private LongFilter estValideParId;

    private LongFilter platId;

    public PanierCriteria() {}

    public PanierCriteria(PanierCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.prixTotal = other.prixTotal == null ? null : other.prixTotal.copy();
        this.constitueId = other.constitueId == null ? null : other.constitueId.copy();
        this.estValideParId = other.estValideParId == null ? null : other.estValideParId.copy();
        this.platId = other.platId == null ? null : other.platId.copy();
    }

    @Override
    public PanierCriteria copy() {
        return new PanierCriteria(this);
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

    public StringFilter getPrixTotal() {
        return prixTotal;
    }

    public StringFilter prixTotal() {
        if (prixTotal == null) {
            prixTotal = new StringFilter();
        }
        return prixTotal;
    }

    public void setPrixTotal(StringFilter prixTotal) {
        this.prixTotal = prixTotal;
    }

    public LongFilter getConstitueId() {
        return constitueId;
    }

    public LongFilter constitueId() {
        if (constitueId == null) {
            constitueId = new LongFilter();
        }
        return constitueId;
    }

    public void setConstitueId(LongFilter constitueId) {
        this.constitueId = constitueId;
    }

    public LongFilter getEstValideParId() {
        return estValideParId;
    }

    public LongFilter estValideParId() {
        if (estValideParId == null) {
            estValideParId = new LongFilter();
        }
        return estValideParId;
    }

    public void setEstValideParId(LongFilter estValideParId) {
        this.estValideParId = estValideParId;
    }

    public LongFilter getPlatId() {
        return platId;
    }

    public LongFilter platId() {
        if (platId == null) {
            platId = new LongFilter();
        }
        return platId;
    }

    public void setPlatId(LongFilter platId) {
        this.platId = platId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PanierCriteria that = (PanierCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(prixTotal, that.prixTotal) &&
            Objects.equals(constitueId, that.constitueId) &&
            Objects.equals(estValideParId, that.estValideParId) &&
            Objects.equals(platId, that.platId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prixTotal, constitueId, estValideParId, platId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PanierCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (prixTotal != null ? "prixTotal=" + prixTotal + ", " : "") +
            (constitueId != null ? "constitueId=" + constitueId + ", " : "") +
            (estValideParId != null ? "estValideParId=" + estValideParId + ", " : "") +
            (platId != null ? "platId=" + platId + ", " : "") +
            "}";
    }
}
