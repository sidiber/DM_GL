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
 * Criteria class for the {@link fr.polytech.info4.domain.Restaurant} entity. This class is used
 * in {@link fr.polytech.info4.web.rest.RestaurantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /restaurants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RestaurantCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomResto;

    private StringFilter fraisLivraison;

    private StringFilter adresseResto;

    private StringFilter codePResto;

    private StringFilter villeResto;

    private LongFilter platId;

    public RestaurantCriteria() {}

    public RestaurantCriteria(RestaurantCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomResto = other.nomResto == null ? null : other.nomResto.copy();
        this.fraisLivraison = other.fraisLivraison == null ? null : other.fraisLivraison.copy();
        this.adresseResto = other.adresseResto == null ? null : other.adresseResto.copy();
        this.codePResto = other.codePResto == null ? null : other.codePResto.copy();
        this.villeResto = other.villeResto == null ? null : other.villeResto.copy();
        this.platId = other.platId == null ? null : other.platId.copy();
    }

    @Override
    public RestaurantCriteria copy() {
        return new RestaurantCriteria(this);
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

    public StringFilter getNomResto() {
        return nomResto;
    }

    public StringFilter nomResto() {
        if (nomResto == null) {
            nomResto = new StringFilter();
        }
        return nomResto;
    }

    public void setNomResto(StringFilter nomResto) {
        this.nomResto = nomResto;
    }

    public StringFilter getFraisLivraison() {
        return fraisLivraison;
    }

    public StringFilter fraisLivraison() {
        if (fraisLivraison == null) {
            fraisLivraison = new StringFilter();
        }
        return fraisLivraison;
    }

    public void setFraisLivraison(StringFilter fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public StringFilter getAdresseResto() {
        return adresseResto;
    }

    public StringFilter adresseResto() {
        if (adresseResto == null) {
            adresseResto = new StringFilter();
        }
        return adresseResto;
    }

    public void setAdresseResto(StringFilter adresseResto) {
        this.adresseResto = adresseResto;
    }

    public StringFilter getCodePResto() {
        return codePResto;
    }

    public StringFilter codePResto() {
        if (codePResto == null) {
            codePResto = new StringFilter();
        }
        return codePResto;
    }

    public void setCodePResto(StringFilter codePResto) {
        this.codePResto = codePResto;
    }

    public StringFilter getVilleResto() {
        return villeResto;
    }

    public StringFilter villeResto() {
        if (villeResto == null) {
            villeResto = new StringFilter();
        }
        return villeResto;
    }

    public void setVilleResto(StringFilter villeResto) {
        this.villeResto = villeResto;
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
        final RestaurantCriteria that = (RestaurantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomResto, that.nomResto) &&
            Objects.equals(fraisLivraison, that.fraisLivraison) &&
            Objects.equals(adresseResto, that.adresseResto) &&
            Objects.equals(codePResto, that.codePResto) &&
            Objects.equals(villeResto, that.villeResto) &&
            Objects.equals(platId, that.platId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomResto, fraisLivraison, adresseResto, codePResto, villeResto, platId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomResto != null ? "nomResto=" + nomResto + ", " : "") +
            (fraisLivraison != null ? "fraisLivraison=" + fraisLivraison + ", " : "") +
            (adresseResto != null ? "adresseResto=" + adresseResto + ", " : "") +
            (codePResto != null ? "codePResto=" + codePResto + ", " : "") +
            (villeResto != null ? "villeResto=" + villeResto + ", " : "") +
            (platId != null ? "platId=" + platId + ", " : "") +
            "}";
    }
}
