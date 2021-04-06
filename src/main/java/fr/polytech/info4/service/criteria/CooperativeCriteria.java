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
 * Criteria class for the {@link fr.polytech.info4.domain.Cooperative} entity. This class is used
 * in {@link fr.polytech.info4.web.rest.CooperativeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cooperatives?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CooperativeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomCoop;

    private StringFilter villeCoop;

    private LongFilter compteId;

    public CooperativeCriteria() {}

    public CooperativeCriteria(CooperativeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomCoop = other.nomCoop == null ? null : other.nomCoop.copy();
        this.villeCoop = other.villeCoop == null ? null : other.villeCoop.copy();
        this.compteId = other.compteId == null ? null : other.compteId.copy();
    }

    @Override
    public CooperativeCriteria copy() {
        return new CooperativeCriteria(this);
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

    public StringFilter getNomCoop() {
        return nomCoop;
    }

    public StringFilter nomCoop() {
        if (nomCoop == null) {
            nomCoop = new StringFilter();
        }
        return nomCoop;
    }

    public void setNomCoop(StringFilter nomCoop) {
        this.nomCoop = nomCoop;
    }

    public StringFilter getVilleCoop() {
        return villeCoop;
    }

    public StringFilter villeCoop() {
        if (villeCoop == null) {
            villeCoop = new StringFilter();
        }
        return villeCoop;
    }

    public void setVilleCoop(StringFilter villeCoop) {
        this.villeCoop = villeCoop;
    }

    public LongFilter getCompteId() {
        return compteId;
    }

    public LongFilter compteId() {
        if (compteId == null) {
            compteId = new LongFilter();
        }
        return compteId;
    }

    public void setCompteId(LongFilter compteId) {
        this.compteId = compteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CooperativeCriteria that = (CooperativeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomCoop, that.nomCoop) &&
            Objects.equals(villeCoop, that.villeCoop) &&
            Objects.equals(compteId, that.compteId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomCoop, villeCoop, compteId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CooperativeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomCoop != null ? "nomCoop=" + nomCoop + ", " : "") +
            (villeCoop != null ? "villeCoop=" + villeCoop + ", " : "") +
            (compteId != null ? "compteId=" + compteId + ", " : "") +
            "}";
    }
}
