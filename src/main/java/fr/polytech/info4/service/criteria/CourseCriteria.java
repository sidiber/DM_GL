package fr.polytech.info4.service.criteria;

import fr.polytech.info4.domain.enumeration.EtatCourse;
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
 * Criteria class for the {@link fr.polytech.info4.domain.Course} entity. This class is used
 * in {@link fr.polytech.info4.web.rest.CourseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /courses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CourseCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EtatCourse
     */
    public static class EtatCourseFilter extends Filter<EtatCourse> {

        public EtatCourseFilter() {}

        public EtatCourseFilter(EtatCourseFilter filter) {
            super(filter);
        }

        @Override
        public EtatCourseFilter copy() {
            return new EtatCourseFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter createdAt;

    private EtatCourseFilter etat;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private LongFilter montantId;

    private LongFilter platId;

    private LongFilter estlivreId;

    public CourseCriteria() {}

    public CourseCriteria(CourseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.etat = other.etat == null ? null : other.etat.copy();
        this.startTime = other.startTime == null ? null : other.startTime.copy();
        this.endTime = other.endTime == null ? null : other.endTime.copy();
        this.montantId = other.montantId == null ? null : other.montantId.copy();
        this.platId = other.platId == null ? null : other.platId.copy();
        this.estlivreId = other.estlivreId == null ? null : other.estlivreId.copy();
    }

    @Override
    public CourseCriteria copy() {
        return new CourseCriteria(this);
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

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            createdAt = new InstantFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public EtatCourseFilter getEtat() {
        return etat;
    }

    public EtatCourseFilter etat() {
        if (etat == null) {
            etat = new EtatCourseFilter();
        }
        return etat;
    }

    public void setEtat(EtatCourseFilter etat) {
        this.etat = etat;
    }

    public InstantFilter getStartTime() {
        return startTime;
    }

    public InstantFilter startTime() {
        if (startTime == null) {
            startTime = new InstantFilter();
        }
        return startTime;
    }

    public void setStartTime(InstantFilter startTime) {
        this.startTime = startTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public InstantFilter endTime() {
        if (endTime == null) {
            endTime = new InstantFilter();
        }
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
    }

    public LongFilter getMontantId() {
        return montantId;
    }

    public LongFilter montantId() {
        if (montantId == null) {
            montantId = new LongFilter();
        }
        return montantId;
    }

    public void setMontantId(LongFilter montantId) {
        this.montantId = montantId;
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

    public LongFilter getEstlivreId() {
        return estlivreId;
    }

    public LongFilter estlivreId() {
        if (estlivreId == null) {
            estlivreId = new LongFilter();
        }
        return estlivreId;
    }

    public void setEstlivreId(LongFilter estlivreId) {
        this.estlivreId = estlivreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CourseCriteria that = (CourseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(etat, that.etat) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(montantId, that.montantId) &&
            Objects.equals(platId, that.platId) &&
            Objects.equals(estlivreId, that.estlivreId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, etat, startTime, endTime, montantId, platId, estlivreId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (etat != null ? "etat=" + etat + ", " : "") +
            (startTime != null ? "startTime=" + startTime + ", " : "") +
            (endTime != null ? "endTime=" + endTime + ", " : "") +
            (montantId != null ? "montantId=" + montantId + ", " : "") +
            (platId != null ? "platId=" + platId + ", " : "") +
            (estlivreId != null ? "estlivreId=" + estlivreId + ", " : "") +
            "}";
    }
}
