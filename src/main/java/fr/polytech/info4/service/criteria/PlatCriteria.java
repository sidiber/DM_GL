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
 * Criteria class for the {@link fr.polytech.info4.domain.Plat} entity. This class is used
 * in {@link fr.polytech.info4.web.rest.PlatResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /plats?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PlatCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomPlat;

    private StringFilter prix;

    private LongFilter restaurantId;

    private LongFilter panierId;

    private LongFilter courseId;

    public PlatCriteria() {}

    public PlatCriteria(PlatCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomPlat = other.nomPlat == null ? null : other.nomPlat.copy();
        this.prix = other.prix == null ? null : other.prix.copy();
        this.restaurantId = other.restaurantId == null ? null : other.restaurantId.copy();
        this.panierId = other.panierId == null ? null : other.panierId.copy();
        this.courseId = other.courseId == null ? null : other.courseId.copy();
    }

    @Override
    public PlatCriteria copy() {
        return new PlatCriteria(this);
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

    public StringFilter getNomPlat() {
        return nomPlat;
    }

    public StringFilter nomPlat() {
        if (nomPlat == null) {
            nomPlat = new StringFilter();
        }
        return nomPlat;
    }

    public void setNomPlat(StringFilter nomPlat) {
        this.nomPlat = nomPlat;
    }

    public StringFilter getPrix() {
        return prix;
    }

    public StringFilter prix() {
        if (prix == null) {
            prix = new StringFilter();
        }
        return prix;
    }

    public void setPrix(StringFilter prix) {
        this.prix = prix;
    }

    public LongFilter getRestaurantId() {
        return restaurantId;
    }

    public LongFilter restaurantId() {
        if (restaurantId == null) {
            restaurantId = new LongFilter();
        }
        return restaurantId;
    }

    public void setRestaurantId(LongFilter restaurantId) {
        this.restaurantId = restaurantId;
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

    public LongFilter getCourseId() {
        return courseId;
    }

    public LongFilter courseId() {
        if (courseId == null) {
            courseId = new LongFilter();
        }
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PlatCriteria that = (PlatCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomPlat, that.nomPlat) &&
            Objects.equals(prix, that.prix) &&
            Objects.equals(restaurantId, that.restaurantId) &&
            Objects.equals(panierId, that.panierId) &&
            Objects.equals(courseId, that.courseId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomPlat, prix, restaurantId, panierId, courseId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlatCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomPlat != null ? "nomPlat=" + nomPlat + ", " : "") +
            (prix != null ? "prix=" + prix + ", " : "") +
            (restaurantId != null ? "restaurantId=" + restaurantId + ", " : "") +
            (panierId != null ? "panierId=" + panierId + ", " : "") +
            (courseId != null ? "courseId=" + courseId + ", " : "") +
            "}";
    }
}
